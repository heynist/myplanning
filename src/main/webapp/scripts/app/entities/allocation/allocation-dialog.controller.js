'use strict';

angular.module('myplanningApp').controller('AllocationDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Allocation', 'Person', 'Company',
        function($scope, $stateParams, $uibModalInstance, entity, Allocation, Person, Company) {

        $scope.allocation = entity;
        $scope.persons = Person.query();
        $scope.companys = Company.query();
        $scope.load = function(id) {
            Allocation.get({id : id}, function(result) {
                $scope.allocation = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('myplanningApp:allocationUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.allocation.id != null) {
                Allocation.update($scope.allocation, onSaveSuccess, onSaveError);
            } else {
                Allocation.save($scope.allocation, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForStartDate = {};

        $scope.datePickerForStartDate.status = {
            opened: false
        };

        $scope.datePickerForStartDateOpen = function($event) {
            $scope.datePickerForStartDate.status.opened = true;
        };
        $scope.datePickerForEndDate = {};

        $scope.datePickerForEndDate.status = {
            opened: false
        };

        $scope.datePickerForEndDateOpen = function($event) {
            $scope.datePickerForEndDate.status.opened = true;
        };
}]);
