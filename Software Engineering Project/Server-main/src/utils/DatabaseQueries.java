package utils;

import game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;
import game.Story;
import student.Student;
import teacher.Teacher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseQueries {

    private final JdbcTemplate jbdcTemplate;

    private final Map<Integer, Story> storyCache = new HashMap<>();
    private final Map<Integer, Game> gameCache = new HashMap<>();
    private final Map<String, Student> studentCache = new HashMap<>();
    private final Map<String, Teacher> teacherByCodeCache = new HashMap<>();

    @Autowired
    public DatabaseQueries(final JdbcTemplate jbdcTemplate) {
        this.jbdcTemplate = jbdcTemplate;
    }

    public Story getStory(final Integer storyId) {
        Story story = this.storyCache.get(storyId);
        if(story == null) {
            story = this.jbdcTemplate.query("SELECT id, story_text, scrambled_words FROM stories WHERE id = ?", new Object[] {storyId},
                    (final ResultSet rs) -> rs.next() ? new Story(rs.getInt(1), rs.getString(2), rs.getString(3)) : null);
            if(story == null)
                return null;
            this.storyCache.put(storyId, story);
        }
        return story;
    }

    public boolean studentExists(final String studentId) throws DataAccessException {
        if(this.studentCache.containsKey(studentId))
            return true;
        Integer count = this.jbdcTemplate.queryForObject("SELECT count(school_student_id) FROM student WHERE school_student_id = ?", new Object[] {studentId}, Integer.class);
        return count != 0;
    }

    public Student getStudent(final String studentId) throws DataAccessException {
        Student student = this.studentCache.get(studentId);
        if(student == null) {
            student = this.jbdcTemplate.query("SELECT id, first_name, last_name, school_student_id, teacher_id, story_index, completed_words, score, game_id FROM student WHERE school_student_id = ?", new Object[]{studentId},
                    (final ResultSet rs) -> rs.next() ? new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), (Integer[]) rs.getArray(7).getArray(), rs.getInt(8), this.getGame(rs.getInt(9))) : null);
            if(student == null)
                return null;
            this.studentCache.put(studentId, student);
        }
        return student;
    }

    public Game getGame(final int gameId) throws DataAccessException {
        Game game = this.gameCache.get(gameId);
        if(game == null) {
            game = this.jbdcTemplate.query("SELECT story_ids, started, ended FROM game WHERE id = ?", new Object[]{gameId},
                    (final ResultSet rs) -> rs.next() ? new Game(gameId, (Integer[]) rs.getArray(1).getArray(), rs.getBoolean(2), rs.getBoolean(3)) : null);
            if(game == null)
                return null;
            this.gameCache.put(gameId, game);
        }
        return game;
    }

    public Teacher getTeacherByCode(final String teacherCode) throws DataAccessException {
        Teacher teacher = this.teacherByCodeCache.get(teacherCode);
        if(teacher == null) {
            teacher = this.jbdcTemplate.query("SELECT id, first_name, last_name, game_id FROM teacher WHERE code = ?", new Object[]{teacherCode},
                    (final ResultSet rs) -> rs.next() ? new Teacher(rs.getInt(1), rs.getString(2), rs.getString(3), this.getGame(rs.getInt(4))) : null);
            if(teacher == null)
                return null;
            this.teacherByCodeCache.put(teacherCode, teacher);
        }
        return teacher;
    }

    public List<Student> getStudentsByTeacherCode(final String teacherCode) {
        final Teacher teacher = this.getTeacherByCode(teacherCode);
        return this.jbdcTemplate.query("SELECT id, first_name, last_name, school_student_id, teacher_id, story_index, completed_words, score, game_id FROM student WHERE teacher_id = ? AND game_id = ?", new Object[]{teacher.teacherId, teacher.getGame().gameId},
                (final ResultSet rs, int rowNum) -> new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6), (Integer[]) rs.getArray(7).getArray(), rs.getInt(8), this.getGame(rs.getInt(9))));
    }

    public Game getGameByTeacherCode(final String teacherCode) {
        return this.getTeacherByCode(teacherCode).getGame();
    }

    public Boolean updateStudentProgress(final Student student) {
        return this.jbdcTemplate.execute("UPDATE student SET story_index = ?, completed_words = ?, score = ? WHERE id = ?", (final PreparedStatement ps) -> {
            ps.setInt(1, student.getStoryIndex());
            ps.setArray(2, ps.getConnection().createArrayOf("integer", student.getSolvedWords()));
            ps.setInt(3, student.getScore());
            ps.setInt(4, student.getDatabaseStudentId());
            return ps.execute();
        });
    }

    public Story addStory(final Game game, final Story story) {
        final int[] scrambledIndex = story.getScrambledWordIndexes();
        final int scrambledWordCount = scrambledIndex.length;
        final StringBuilder indexes = new StringBuilder(scrambledWordCount * 2);
        for(int i=0; i< scrambledIndex.length; i++) {
            indexes.append(scrambledIndex[i] + (i < scrambledWordCount - 1 ? "," : ""));
        }
        story.setId(this.jbdcTemplate.queryForObject("INSERT INTO  stories (story_text, scrambled_words) VALUES (?, ?) RETURNING id", new Object[]{story.getSolvedStory(), indexes.toString()}, Integer.class));
        game.addStory(story);
        this.jbdcTemplate.execute("UPDATE game SET story_ids = ? WHERE id = ?", (final PreparedStatement ps) -> {
            ps.setArray(1, ps.getConnection().createArrayOf("integer", game.getStoryIds()));
            ps.setInt(2, game.gameId);
            return ps.execute();
        });
        return story;
    }

    public Student addStudent(final Student student) {
        this.jbdcTemplate.execute("INSERT INTO student (first_name, last_name, school_student_id, teacher_id, story_index, completed_words, score, game_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", (final PreparedStatement ps) -> {
            ps.setString(1, student.firstName);
            ps.setString(2, student.lastName);
            ps.setString(3, student.schoolStudentId);
            ps.setInt(4, student.teacherId);
            ps.setInt(5, student.getStoryIndex());
            ps.setArray(6, ps.getConnection().createArrayOf("integer", student.getSolvedWords()));
            ps.setInt(7, student.getScore());
            ps.setInt(8, student.getGame().gameId);
            return ps.execute();
        });
        student.setDatabaseStudentId(this.jbdcTemplate.queryForObject("SELECT lastval()", Integer.class));
        this.studentCache.put(student.schoolStudentId, student);
        return student;
    }

    public Game updateGameStatus(final Game game) {
        if(game != null)
            this.jbdcTemplate.execute("UPDATE game SET started = ?, ended = ?", (final PreparedStatement ps) -> {
                ps.setBoolean(1, game.hasStarted());
                ps.setBoolean(2, game.hasEnded());
                return ps.execute();
            });
        return game;
    }

    public Game resetGame(final Game game) {
        this.updateGameStatus(game);
        this.jbdcTemplate.execute("UPDATE student SET story_index = ?, completed_words = ?, score = ? WHERE game_id = ?", (final PreparedStatement ps) -> {
            ps.setInt(1, 0);
            ps.setArray(2, ps.getConnection().createArrayOf("integer", new Integer[0]));
            ps.setInt(3, 0);
            ps.setInt(4, game.gameId);
            return ps.execute();
        });
        this.studentCache.clear();
        return game;
    }
}
