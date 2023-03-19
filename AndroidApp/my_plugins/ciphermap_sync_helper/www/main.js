var exec = require('cordova/exec');

exports.start = function (arg0, success, error) {
    exec(success, error, 'ciphermap_sync_helper', 'start', [arg0]);
};
exports.stop = function (success) {
    exec(success, null, 'ciphermap_sync_helper', 'stop', [null]);
};
exports.bindCallback = function (success) {
    exec(success, null, 'ciphermap_sync_helper', 'bindCallback', [null]);
};