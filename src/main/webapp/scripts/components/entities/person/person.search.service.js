'use strict';

angular.module('myplanningApp')
    .factory('PersonSearch', function ($resource) {
        return $resource('api/_search/persons/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
