var stressorApp = angular.module('stressor', ['ngRoute']);
 
stressorApp.config(['$routeProvider',
  function($routeProvider) {
	$routeProvider.
		when('/', {
			templateUrl: 'start.html',
			controller: 'StartController'
		}).
		when('/status/:testDefinition/:timestamp', {
			templateUrl: 'status.html',
			controller: 'StatusController'
		}).
		when('/status/:testDefinition', {
			templateUrl: 'status.html',
			controller: 'StatusController'
		}).
		when('/status', {
			templateUrl: 'status.html',
			controller: 'StatusController'
		}).
		when('/report', {
			templateUrl: 'report.html'
		}).
		otherwise({
			redirectTo: '/'
		});
  }
]);

stressorApp.factory('errors', function() {
	var service = {};
	service.errors = [];
	service.getErrors = function() {
		return this.errors;
	}
	service.push = function(arg) {
		this.errors.push(arg);
	}
	service.remove = function(pos) {
		this.errors.splice(pos, 1);
	}
	return service;
});

stressorApp.controller('NavController', function($scope, $location, $http, $log, $route, errors) {
	$scope.errors = errors;
});

stressorApp.controller('StartController', function($scope, $location, $http, $log, $route, errors) {
	$scope.tab = "";
	$scope.test = {};
	
	$scope.setTestDefinition = function(canonicalName) {
		$scope.test.definition = canonicalName;
	}
	
	$scope.testDefinitions = [];
	$scope.testDefinitionsLoading = false;

	$scope.startTest = function() {
		$http.post('rest/loadtest/' + $scope.test.definition, $scope.test).
		success(function(data,status,headers,config) {
			$log.log(headers('location'));
			$location.path('status/' + $scope.test.definition + "/" + data);
		}).
		error(function(data, status, headers, config) {
			errors.push(data);
		});
	}
	
	$scope.onLoad = function() {
		$scope.testDefinitionsLoading = true;
		$http.get('rest/loadtestdefinition').
		success(function(data,status,headers,config) {
			$scope.testDefinitionsLoading = false;
			$scope.testDefinitions = data;
		}).
		error(function(data, status, headers, config) {
			$scope.testDefinitionsLoading = false;
			errors.push(data);
		});
	}
	$scope.onLoad();
});

stressorApp.controller('StatusController', function($scope, $location, $http, $log, $route, $timeout, errors) {
	$scope.status = [];

	$scope.getStatus = function() {
		$http.get('rest/loadtest/' + $route.current.params.testDefinition + "/" + $route.current.params.timestamp).
		success(function(data,status,headers,config) {
			$scope.status = data;
			if($scope.status.status == 'RUNNING' || $scope.status.status == 'VALIDATING' || $scope.status.status == 'PENDING') {
				$timeout($scope.getStatus, 1000);
			}
		}).
		error(function(data, status, headers, config) {
			errors.push(data);
		});
	}
	
	$scope.getStatus();
});