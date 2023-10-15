# Coding Challenge

### Implemented below endpoints

getAllEmployees()

    output - list of employees
    description - returns all employees list

getEmployeesByNameSearch()

    output - list of employees
    description - returns all employees whose name contains or matches the string input provided

getEmployeeById(string id)

    output - employee
    description - returns a single employee

getHighestSalaryOfEmployees()

    output - integer of the highest salary
    description -  returns a single integer indicating the highest salary of all employees

getTop10HighestEarningEmployeeNames()

    output - list of employees
    description - returns a list of the top 10 employees based off of their salaries

createEmployee(string name, string salary, string age)

    output - string of the status (i.e. success)
    description - returns a status of success or failed based on if an employee was created

deleteEmployee(String id)

    output - the name of the employee that was deleted
    description - deletes the employee with specified id given and returns its name.

### Tests

For all above implemented methods, Written tests inside the class `RqChallengeApplicationTests`.

### Area of Improvement
1. The JSON mapping of responses we are doing can be converted into a respective mapping classes.
2. Common code onside the `EmployeeUtil` can be pulled out and made reusable.
3. As of now the tests are only written for happy path, there should inclusion of failure detection corner case tests.

### Existing Boilerplate
Keeping an existing boilerplate code untouched like name of the test class `RqChallengeApplicationTests` and RestController interface `IEmployeeController`.

