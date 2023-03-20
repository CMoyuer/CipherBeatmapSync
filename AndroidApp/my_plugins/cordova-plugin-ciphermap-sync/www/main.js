var exec = require('cordova/exec');

exports.start = function (success, error) {
    exec(success, error, 'ciphermap_sync_helper', 'start', [null]);
};
exports.stop = function (success) {
    exec(success, null, 'ciphermap_sync_helper', 'stop', [null]);
};
exports.bindCallback = function (success) {
    exec(success, null, 'ciphermap_sync_helper', 'bindCallback', [null]);
};