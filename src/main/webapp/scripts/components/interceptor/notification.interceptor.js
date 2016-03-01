 'use strict';

angular.module('myplanningApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-myplanningApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-myplanningApp-params')});
                }
                return response;
            }
        };
    });
