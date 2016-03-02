'use strict';

angular.module('myplanningApp')
    .factory('ContractSearch', function ($resource) {
        return $resource('api/_search/contracts/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
