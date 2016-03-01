'use strict';

angular.module('myplanningApp')
	.controller('PersonDeleteController', function($scope, $uibModalInstance, entity, Person) {

        $scope.person = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Person.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
