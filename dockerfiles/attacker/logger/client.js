const request = require('request');

function sendRequest() {
    let msg = "ajout";
    /*request.get(
        'http://localhost:3000/log?msg=' + msg,
        { json: { key: 'value' } },
        function (error, response, body) {
            if (!error && response.statusCode === 200) {
                console.log(body);
            }
        }
    );*/
}

module.exports = {
    sendRequest
};