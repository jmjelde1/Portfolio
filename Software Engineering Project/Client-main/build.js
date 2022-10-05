/**
 * This is a NodeJS script, NOT a browser script. It will not run in the browser.
 * To run: `node build.js serve` or `node build.js serve nonverbose`.
 * Dependencies: `terser` package (install prior to running).
 * Modified from iiod.io - build.js.
 * @author Timur Tripp
 */
const
    version = '1.0',
    buildCode = 'r1',
    serverPort = 3333,
    orderedGameJSFiles = [
        // Put ALL JS fules in the /src/js folder to be combined into a single file here. Make sure to order according to dependencies.
        'Utilities.js',
        'Word.js',
        'Story.js'
        

    ],
    fs = require('fs'), terser = require('terser'),
    LOG_SUCCESS = {
        prefix: "\u2714 ",
        color: "#0B0"
    },
    LOG_FAILURE = {
        prefix: "\u2718 ",
        color: "#D00"
    },
    LOG_NEUTRAL = {
        prefix: "",
        color: "#0BB"
    },
    LOG_DEBUG = {
        prefix: "[debug mode] ",
        color: "default"
    },
    JS_CONTENT_TYPE = 'text/javascript', CSS_CONTENT_TYPE = 'text/css', HTML_CONTENT_TYPE = 'text/html', TEXT_CONTENT_TYPE = 'text/plain', JSON_CONTENT_TYPE = 'application/json',
    constants = {gameName : 'spelling game', buildCode : buildCode, VERSION : version, DEBUG : false, LOCAL : false, LOG_SUCCESS : LOG_SUCCESS, LOG_FAILURE : LOG_FAILURE, LOG_NEUTRAL : LOG_NEUTRAL, LOG_DEBUG : LOG_DEBUG};

function getGameJS(){
    // let code = '"use strict";';
    let code = '';
    orderedGameJSFiles.forEach(function(fileName){
        code += "\n/* " + fileName + " */\n" + fs.readFileSync('src/js/' + fileName, 'utf-8');
    });
    return code;
}

async function beautifyJS(js, preamble) {
    const options = {
        mangle: false,
        compress: {
            defaults: false,
            global_defs: constants
        },
        output: {
            beautify: true,
            comments: true,
            preamble: '/* ' + preamble + ' */',
            quote_style: 3
        }
    };
    return await terser.minify(js, options);
}

async function minifyJS(js, preamble) {
    const options = {
        mangle: true,
        compress: {
            defaults: false,
            global_defs: constants
        },
        output: {
            beautify: false,
            comments: false,
            preamble: '/* ' + preamble + ' */',
            quote_style: 3
        }
    };
    return await terser.minify('(function(){' + js + '})();', options);
}

async function getFileOutput(requestFile, nonVerbose){
    let code = 0, type = TEXT_CONTENT_TYPE, text = '';
    switch(requestFile){
        case 'story.js':
            var preamble = 'story.js build ' + buildCode + '\n * THE STORYTELLERS\n * CMPT 322 Software Engineering', raw = getGameJS(), result = nonVerbose ? await minifyJS(raw, preamble) : await beautifyJS(raw, preamble);
            if(result.error)
                console.log(result.error),
                code = 500,
                type = JS_CONTENT_TYPE,
                text = '// '+ requestFile + ' build failed';
            else
                code = 200,
                type = JS_CONTENT_TYPE,
                text = result.code;
        break;
        case 'styles.css':
            code = 200;
            type = CSS_CONTENT_TYPE;
            text = fs.readFileSync('src/css/styles.css', 'utf-8');
        break;
        case '':
        case 'index.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/index.html', 'utf-8');
        break;
        case 'story.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/story.html', 'utf-8');
        break;
        case 'teacherChoose.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/teacherChoose.html', 'utf-8');
        break;
        case 'addStory.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/addStory.html', 'utf-8');
        break;
        case 'studentProgress.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/studentProgress.html', 'utf-8');

        break;
        case 'neemSelectS.html':
                code = 200;
                type = HTML_CONTENT_TYPE;
                text = fs.readFileSync('src/neemSelectS.html', 'utf-8');
        break;
        case 'addStudent.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/addStudent.html', 'utf-8');
        break;
        case 'SelectWords.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/SelectWords.html', 'utf-8');
        break;
        case 'scrambleWords.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/scrambleWords.html', 'utf-8');
        break;
        case 'editStory.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/editStory.html', 'utf-8');
        break;
        case 'previewStory.html':
            code = 200;
            type = HTML_CONTENT_TYPE;
            text = fs.readFileSync('src/previewStory.html', 'utf-8');
    }
    code == 0 ? console.log('Unrecognized file `' + requestFile + '`, output is empty.') : console.log('Output file `' + requestFile + '`.');
    return {
        code: code,
        type: type,
        text: text
    };
}

function initServer(nonVerbose){
    constants.DEBUG = constants.LOCAL = true;
    const
        http = require('http'),
        server = http.createServer(async function(request, response){
            let fileResponse = await getFileOutput(request.url.substr(1), nonVerbose), code = fileResponse.code, type = fileResponse.type, text = fileResponse.text;
            if(code == 0)
                code = 404,
                type = HTML_CONTENT_TYPE,
                text = "<!DOCTYPE HTML>\n<html>\n<head>\n<title>Error 404</title>\n</head>\n<body>\n<h1>Error 404</h1>\n<p>The requested file isn't recognized.</p>\n</body>\n</html>";
            response.writeHead(code, {'Content-Type': type});
            response.write(text);
            response.end();
        });
    server.listen(serverPort);
    console.log('Started server, the iiod.io client should now be accessible over port ' + serverPort + '.' + (nonVerbose ? '\nnonverbose: The HTML page should reference the minified JS and CSS code.': ''));
}

const passedArgs = process.argv;
/* if(passedArgs[2] == 'make')
    make();
else */if(passedArgs[2] == 'serve')
    initServer(passedArgs[3] == 'nonverbose');
else console.log('Pass either `make` to save built iiod.io client code, or `serve` to serve it over port ' + serverPort + '. `serve` may be followed by `nonverbose` to force the HTML page to reference the minified JS and CSS code.');