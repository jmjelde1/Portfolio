var activeWord, dragStartElement;

function Word(clickableWordElement, scrambledWordIndex, unsolvedWord, solvedWord, isSolved) {
    this.clickableWordElement = clickableWordElement;
    this.scrambledWordIndex = scrambledWordIndex;
    this.unsolvedWord = unsolvedWord;
    this.solvedWord = solvedWord;
    this.isSolved = isSolved;
    this.studentSolution = isSolved ? solvedWord : unsolvedWord;
    this.hintsUsed = 0;
    this.hintLastUsedTs = 0;
    this.showHintButtonTimeout = null;
    if(isSolved) {
        clickableWordElement.className = 'word solvable solved';
        clickableWordElement.innerText = solvedWord;
    } else {
        clickableWordElement.className = 'word solvable unsolved';
        clickableWordElement.innerText = unsolvedWord;
    }
    clickableWordElement.onclick = (function(event) {
        this.showWord();
    }).bind(this);
}

Word.prototype = {
    makeSolved: function() {
        this.isSolved = true;
        this.studentSolution = this.solvedWord;
        if(activeWord == this)
            this.showLettersSolved(this.solvedWord.length);
        this.clickableWordElement.className = 'word solvable solved';
        this.clickableWordElement.innerText = this.studentSolution;
        if(this.showHintButtonTimeout)
            clearTimeout(this.showHintButtonTimeout);
        document.getElementById('hint').style.visibility = 'none';
    },
    showLettersSolved: function(letterCount) {
        const letters = document.querySelectorAll('.letters'), word = this.solvedWord;
        for (var i = 0; i < letterCount; i++) {
            let found = false;
            for (const l of letters) {
                if (l.textContent === word[i] && !found) {
                    this.swapLetters(i, parseInt(l.dataset['letterIndex']));
                    l.textContent = letters[i].textContent;
    
                    letters[i].textContent = word[i];
                    letters[i].className += ' lettershint';
                    let par = letters[i].parentNode;
                    par.className ="box2";
                    found = true;
    
                    letters[i].setAttribute('draggable', false);
                    letters[i].dataset['solved'] = 'true';
                }
            }
        }
    },
    beginHintTimer: function () {
        const hintButtonElement = document.getElementById('hint');
        hintButtonElement.style.visibility = 'hidden';
        hintButtonElement.onclick = null;
        if(this.hintsUsed >= Math.min(this.solvedWord.length, 3))
            this.checkStudentSolution();
        else {
            const timeToHint = this.hintLastUsedTs - Date.now() + 10000, showHintButton = (function() {
                hintButtonElement.style.visibility = 'visible';
                hintButtonElement.onclick = (function() {
                    this.hintsUsed++;
                    this.showHint();
                }).bind(this);
            }).bind(this);
            if(timeToHint > 0)
                this.showHintButtonTimeout = setTimeout(showHintButton, timeToHint);
            else showHintButton();
        }
    },
    showWord: function() {
        if(this.isSolved) // Already solved, do nothing.
            return false;
        
        if(activeWord == this)
            return true;

        if(activeWord && activeWord.showHintButtonTimeout)
            clearTimeout(activeWord.showHintButtonTimeout);
        
        activeWord = this;
        
        const num = this.unsolvedWord.length, word = this.studentSolution, wordContainer = document.getElementById('wordContainer');
    
        wordContainer.innerHTML = '';
        // Add a specific number of boxes in the html file.
        for (var i = 0; i < num; i++) {
            wordContainer.innerHTML += '<div class="box"></div>';
        }
    
        const empties = document.querySelectorAll('.box');
    
        for (var i = 0; i < empties.length; i++) 
            empties[i].innerHTML += '<div class="letters" draggable="true" data-solved="false" data-letter-index="' + i + '">' + word[i] + '</div>';
    
    
        const letters = document.querySelectorAll('.letters');
    
        const swapLetters = this.swapLetters.bind(this), checkStudentSolution = this.checkStudentSolution.bind(this);
        for (const l of letters) {
            l.ondragover = function(event) {
                if(dragStartElement == this || this.dataset['solved'] == 'true') return;
                event.preventDefault();
                this.parentNode.className = 'box hovered';
            };
            l.ondragleave = function() {
                this.parentNode.className = 'box';
            };
            l.ondragstart = function() {
                dragStartElement = this;
            };
            l.ondrop = function(event) {
                const newIndex = parseInt(this.dataset['letterIndex']), oldIndex = parseInt(dragStartElement.dataset['letterIndex']), studentSolution = swapLetters(oldIndex, newIndex);
                this.innerText = studentSolution[newIndex];
                dragStartElement.innerText = studentSolution[oldIndex];
                this.parentNode.className = 'box';
                checkStudentSolution();
            };
        }    

        if(this.hintLastUsedTs == 0)
            this.hintLastUsedTs = Date.now();
        if(this.hintsUsed > 0)
            this.showHint();
        else this.beginHintTimer();

        return true;
    },
    showHint: function() {
        const num = this.solvedWord.length;
        this.showLettersSolved(Math.min(this.hintsUsed * 2, num));    
        this.hintLastUsedTs = Date.now();
        this.beginHintTimer();
        this.checkStudentSolution();
    },
    swapLetters: function(oldIndex, newIndex) {
        var str = this.studentSolution;
        str = str.substring(0, oldIndex) + this.studentSolution[newIndex] + str.substring(oldIndex + 1);
        str = str.substring(0, newIndex) + this.studentSolution[oldIndex] + str.substring(newIndex + 1);
        this.clickableWordElement.innerText = this.studentSolution = str;
        return str;
    },
    checkStudentSolution: function() {
        document.getElementById('studentSolutionWordIndex').value = this.scrambledWordIndex;
        document.getElementById('studentSolutionWordSolution').value = this.studentSolution;    
        requestJSON(
            studentApiUrl,
            (function (responseObject) {
                console.log(responseObject);
                if(responseObject['result'] == null){
                    if(responseObject['problems'] == 'GAME_OVER') {
                        alert("The game has ended");
                        //TODO: Direct to the webpage that shows the students scores.
                        document.getElementById('ref').style.display = 'block';
                        document.getElementById('main_div').style.display = 'none';
                        document.getElementById('wordContainer').style.display = 'none';
                        document.getElementById('storyContainer').style.display = 'none';
                        document.getElementById('score').innerText = "";
                        document.getElementById('logout').style.display = 'none';
                        document.getElementById('hint').style.visibility = 'none';
                        //document.getElementById('logout').style.display = 'none';

                        document.getElementById('attention').style.display = 'block';
                        document.getElementById('attention').innerText = "The game has ended. Please wait for the teachers instruction!";

                    }
                    console.log(responseObject);
                } else {
                    this.makeSolved();
                    refreshStory(responseObject['result']['storyIndex'], responseObject['result']['story']['unsolvedStory'], responseObject['result']['story']['solvedStory'],
                    responseObject['result']['story']['solvableWordIndexes'], responseObject['result']['solvedWords']);

                    document.getElementById("score").innerText = "score: " + responseObject['result']['score'];

                    //const span2 = document.getElementById('pspan');
                    w = parseInt(responseObject['result']['solvedWords'].length) *
                        100/ parseInt(responseObject['result']['story']['solvableWordIndexes'].length);

                    if(w == 0) {
                        const span2 = document.getElementById('pspan');
                        span2.style.width = parseInt(responseObject['result']['solvedWords'].length) *
                           100/ parseInt(responseObject['result']['story']['solvableWordIndexes'].length) + "%";
                    }
                    else {
                        move(w);
                    }


                    if(responseObject['result']['gameEnded'] == true) {
                        document.getElementById('main_div').style.display = 'none';
                        document.getElementById('wordContainer').style.display = 'none';
                        document.getElementById('storyContainer').style.display = 'none';
                        document.getElementById('hint').style.visibility = 'none';
                        //document.getElementById('logout').style.display = 'none';

                        document.getElementById('attention').style.display = 'block';
                        document.getElementById('attention').innerText = "Congratulations! you have completed the game. Please wait for the teachers instruction!";
                        //TODO: Direct to a webpage that shows the student scores and possibly all the solved words.
                    }
                }
            }).bind(this), function () {
                log('Oh no, the student data failed to load!', LOG_FAILURE);
            }, false,
            document.getElementById('studentSolution')
        );    
    }
};