'use strict';

angular.module('myplanningApp').controller('CompanyDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Company', 'Contract', 'User',
        function($scope, $stateParams, $uibModalInstance, entity, Company, Contract, User) {

        $scope.company = entity;
        $scope.contracts = Contract.query();
        $scope.users = User.query();
        $scope.load = function(id) {
            Company.get({id : id}, function(result) {
                $scope.company = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('myplanningApp:companyUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.company.id != null) {
                Company.update($scope.company, onSaveSuccess, onSaveError);
            } else {
                Company.save($scope.company, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
