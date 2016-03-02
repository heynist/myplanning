'use strict';

angular.module('myplanningApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


