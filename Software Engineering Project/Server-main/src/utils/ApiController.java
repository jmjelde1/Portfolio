package utils;

import game.Game;
import game.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import student.Student;
import teacher.Teacher;

import java.util.List;

@RestController
@CrossOrigin
public class ApiController {

    private final DatabaseQueries querier;

    @Autowired
    public ApiController(final DatabaseQueries querier) {
        this.querier = querier;
    }

    @RequestMapping(value = "student", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Student> getStudent(@RequestParam("student-id") final String studentId) {
        return new ReturnTemplate<Student>().validateAndProcessRequest(new InputValidator[]{new InputValidator(studentId)}, () -> this.querier.getStudent(studentId));
    }

    @RequestMapping(value = "student", method = RequestMethod.POST, params = {"student-id", "word-index", "word-solution"})
    public @ResponseBody ReturnTemplate<Student> getSolution(@RequestParam("student-id") final String studentId, @RequestParam("word-index") final int solvableWordIndex, @RequestParam("word-solution") final String studentSolution) {
        final Student student = this.querier.getStudent(studentId);
        return new ReturnTemplate<Student>().validateAndProcessRequest(new InputValidator[]{new SolutionInputValidator(studentSolution, solvableWordIndex, student)}, () -> {
            this.querier.updateStudentProgress(student);
            return student;
        });
    }


    @RequestMapping(value = "teacher", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Teacher> getTeacher(@RequestParam("teacher-code") final String teacherCode) {
        return new ReturnTemplate<Teacher>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.getTeacherByCode(teacherCode));
    }

    @RequestMapping(value = "teacher/students", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<List<Student>> getTeacherStudents(@RequestParam("teacher-code") final String teacherCode) {
        return new ReturnTemplate<List<Student>>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.getStudentsByTeacherCode(teacherCode));
    }

    @RequestMapping(value = "teacher/add-student", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Student> teacherAddStudent(@RequestParam("teacher-code") final String teacherCode, @RequestParam("first-name") final String firstName, @RequestParam("last-name") final String lastName, @RequestParam("student-id") final String schoolStudentId) {
        final Teacher teacher = this.querier.getTeacherByCode(teacherCode);
        return new ReturnTemplate<Student>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)},
                () -> this.querier.studentExists(schoolStudentId) ? null : this.querier.addStudent(new Student(0, firstName, lastName, schoolStudentId, teacher.teacherId, 0, new Integer[0], 0, teacher.getGame())));
    }

    @RequestMapping(value = "teacher/game", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Game> getTeacherGame(@RequestParam("teacher-code") final String teacherCode) {
        return new ReturnTemplate<Game>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.getGameByTeacherCode(teacherCode));
    }

    @RequestMapping(value = "teacher/game/start", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Game> startTeacherGame(@RequestParam("teacher-code") final String teacherCode) {
        return new ReturnTemplate<Game>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.updateGameStatus(this.querier.getGameByTeacherCode(teacherCode).start()));
    }

    @RequestMapping(value = "teacher/game/end", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Game> endTeacherGame(@RequestParam("teacher-code") final String teacherCode) {
        return new ReturnTemplate<Game>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.updateGameStatus(this.querier.getGameByTeacherCode(teacherCode).end()));
    }

    @RequestMapping(value = "teacher/game/reset", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Game> resetTeacherGame(@RequestParam("teacher-code") final String teacherCode) {
        return new ReturnTemplate<Game>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.resetGame(this.querier.getGameByTeacherCode(teacherCode).reset()));
    }

    @RequestMapping(value = "teacher/add-story", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Story> addStory(@RequestParam("teacher-code") final String teacherCode, @RequestParam("story") final String storyText, @RequestParam("scrambled-words") final String scrambledWordIndexes) {
        return new ReturnTemplate<Story>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> this.querier.addStory(this.querier.getTeacherByCode(teacherCode).getGame(), new Story(0, storyText, scrambledWordIndexes)));
    }

    /* @RequestMapping(value = "teacher/edit-story", method = RequestMethod.POST)
    public @ResponseBody ReturnTemplate<Story> addStory(@RequestParam("teacher-code") final String teacherCode, @RequestParam("story") final int storyIndex, @RequestParam("story") final String storyText, @RequestParam("scrambled-words") final String scrambledWordIndexes) {
        final Teacher teacher = this.querier.getTeacherByCode(teacherCode);
        final Game game = teacher == null ? null : teacher.getGame();
        final Story story = game == null ? null : game.getStory(storyIndex);
        return new ReturnTemplate<Story>().validateAndProcessRequest(new InputValidator[]{new InputValidator(teacherCode)}, () -> {
            final Game game = this.querier.getTeacherByCode(teacherCode).getGame();
            this.querier.editStory(game, this.querier.getTeacherByCode(teacherCode).getGame().getStory(storyIndex))
        });
    } */

}