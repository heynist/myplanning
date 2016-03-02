'use strict';

describe('Controller Tests', function() {

    describe('Allocation Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAllocation, MockPerson, MockCompany;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAllocation = jasmine.createSpy('MockAllocation');
            MockPerson = jasmine.createSpy('MockPerson');
            MockCompany = jasmine.createSpy('MockCompany');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Allocation': MockAllocation,
                'Person': MockPerson,
                'Company': MockCompany
            };
            createController = function() {
                $injector.get('$controller')("AllocationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'myplanningApp:allocationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
