'use strict';

angular.module('myplanningApp')
    .controller('PersonDetailController', function ($scope, $rootScope, $stateParams, entity, Person, Contract) {
        $scope.person = entity;
        $scope.load = function (id) {
            Person.get({id: id}, function(result) {
                $scope.person = result;
            });
        };
        var unsubscribe = $rootScope.$on('myplanningApp:personUpdate', function(event, result) {
            $scope.person = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
