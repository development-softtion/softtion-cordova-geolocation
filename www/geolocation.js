
var cordova = require('cordova'), channel = require('cordova/channel');

var geolocation = {
    hasPermission : function(successCallback, errorCallback){
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'hasPermission', []);
    },
    requestPermission : function(successCallback, errorCallback){
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'requestPermission', []);
    },
    openLocationSettings : function(successCallback, errorCallback){
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'openLocationSettings', []);
    },
    getAllProviders : function(successCallback, errorCallback){
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'getAllProviders', []);
    },
    getEnabledProviders : function(successCallback, errorCallback){
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'getEnabledProviders', []);
    },
    isLocationAvailable : function(successCallback, errorCallback){
        return cordova.exec(ensureBoolean(successCallback), errorCallback, 'Geolocation', 'isLocationAvailable', []);
    },
    isLocationEnabled : function(successCallback, errorCallback){
        return cordova.exec(ensureBoolean(successCallback), errorCallback, 'Geolocation', 'isLocationEnabled', []);
    },
    isGpsAvailable : function(successCallback, errorCallback){
        return cordova.exec(ensureBoolean(successCallback), errorCallback, 'Geolocation', 'isGpsAvailable', []);
    },
    isGpsEnabled : function(successCallback, errorCallback){
        return cordova.exec(ensureBoolean(successCallback), errorCallback, 'Geolocation', 'isGpsEnabled', []);
    },
    isNetworkAvailable : function(successCallback, errorCallback){
        return cordova.exec(ensureBoolean(successCallback), errorCallback, 'Geolocation', 'isNetworkAvailable', []);
    },
    isNetworkEnabled : function(successCallback, errorCallback){
        return cordova.exec(ensureBoolean(successCallback), errorCallback, 'Geolocation', 'isNetworkEnabled', []);
    },
    enableLocationListener : function(successCallback, errorCallback, options){
        var minTime = 0;
        var minDistance = 0;
        if(options){
            minTime = options.minTime || 0;
            minDistance = options.Distance || 0;
        }
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'enableLocationListener', [minTime, minDistance]); 
    },
    disableLocationListener : function(successCallback, errorCallback){
        return cordova.exec(successCallback, errorCallback, 'Geolocation', 'disableLocationListener', []); 
    }
};

function ensureBoolean(callback){
    return function(result){callback(!!result);};
};

module.exports = geolocation;