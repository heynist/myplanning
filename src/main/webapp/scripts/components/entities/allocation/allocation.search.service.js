'use strict';

angular.module('myplanningApp')
    .factory('AllocationSearch', function ($resource) {
        return $resource('api/_search/allocations/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
