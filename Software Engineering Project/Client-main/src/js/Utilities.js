/**
 * Outputs a fancy colored log message.
 * Taken from iiod.io - EssentialUtilities.js
 * @author Timur Tripp
 * @param {*} message 
 * @param {object} type 
 */
function log(message, type) {
    console.log('%c' + '[' + gameName + ' build ' + buildCode + '] ' + type.prefix + message, 'color:' + type.color);
}

/**
 * Log function for debug mode.
 * Taken from iiod.io - EssentialUtilities.js
 * @author Timur Tripp
 * @param {*} message 
 */
function debLog(message) {
    log(message, LOG_DEBUG);
}

/**
 * Taken from iiod.io - EssentialUtilities.js
 * @author Timur Tripp
 * @param {string} url 
 * @returns {boolean} true if the url starts with `data:`
 */
function isDataURL(url) {
    return url.substr(0, 5) == 'data:';
}

/**
 * Requests a JSON file / API and returns it as an object.
 * Taken from iiod.io - EssentialUtilities.js
 * @author Timur Tripp
 * @param {String} url 
 * @param {Function} onSuccessFunction 
 * @param {Function} onErrorFunction 
 * @param {Boolean} softTimeout 
 * @param {HTMLFormElement} formToSubmit
 */
function requestJSON(url, onSuccessFunction, onErrorFunction, softTimeout, formToSubmit) {
    const
        dataURL = isDataURL(url),
        timeout = 250000; // The amount of time (ms) that must pass before the request times out.
    if (window.fetch) { // The fetch API is the newer, cleaner way to do this.
        const request = new Request(url),
            logURL = dataURL ? 'a massive data url that can not be comprehended by mere mortals has' : request.url;
        new Promise(function (resolve, reject) {
            fetch(request, formToSubmit ? {
                'method': 'POST',
                'headers': {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                'body': new URLSearchParams(new FormData(formToSubmit)).toString()
                
            } : {
                'method': 'GET'
            }).then(function (response) {
                return response.json();
            }).then(function (data) {
                if (softTimeout) // We're going to declare success here if a soft timeout is desired. This ensures it will run if the parent Promise has already been rejected (due to a timeout).
                    log('fetch: ' + logURL + ' loaded.', LOG_SUCCESS),
                    onSuccessFunction && onSuccessFunction(data);
                resolve(data);
            }).catch(function (error) {
                reject(error);
            });
            setTimeout(function () {
                reject(new Error('The request took longer than ' + timeout + 'ms and timed out')); // This will do nothing if the parent Promise has already been resolved (due to a successful request).
            }, timeout);
        }).then(function (data) {
            if (!softTimeout)
                log('fetch: ' + logURL + ' loaded.', LOG_SUCCESS),
                onSuccessFunction && onSuccessFunction(data);
        }).catch(function (error) {
            log('fetch: ' + logURL + ' ' + LOG_FAILURE.prefix + error + '.', LOG_FAILURE);
            onErrorFunction && onErrorFunction();
            throw error;
        });
    } else { // Dinosaur game, dinosaur browser. Fallback to XMLHttpRequest.
        var request = new XMLHttpRequest(), onTimeoutFunction = function () {
            log('XMLHttpRequest (Legacy): The request timed out.', LOG_FAILURE);
            onErrorFunction && onErrorFunction();
        }, timer;
        if (softTimeout) timer = setTimeout(onTimeoutFunction, timeout);
        // else request.timeout = timeout, request.ontimeout = onTimeoutFunction;
        request.onreadystatechange = function () {
            const logURL = dataURL ? 'a massive data url that can not be comprehended by mere mortals has' : (this.responseURL || url);
            if (this.readyState === 4 && this.status === 200) {
                timer && clearTimeout(timer);
                try {
                    const responseObj = JSON.parse(this.responseText);
                    onSuccessFunction && onSuccessFunction(responseObj);
                    log('XMLHttpRequest (Legacy): ' + logURL + ' loaded.', LOG_SUCCESS);
                } catch (e) {
                    log('XMLHttpRequest (Legacy): ' + logURL + ' failed to parse.', LOG_FAILURE);
                    onErrorFunction && onErrorFunction();
                    return;
                }
            } else if (this.readyState === 4 && this.status !== 200)
                timer && clearTimeout(timer),
                    log('XMLHttpRequest (Legacy): ' + logURL + ' returned status code ' + this.status + '.', LOG_FAILURE),
                onErrorFunction && onErrorFunction();
        };
        if(formToSubmit) {
            request.open('POST', url);
            
            request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
           
            request.send(new URLSearchParams(new FormData(formToSubmit)).toString());
        } else {
            request.open('GET', url);
            request.send();
        }
    }
}