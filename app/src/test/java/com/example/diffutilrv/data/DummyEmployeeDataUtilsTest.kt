package com.example.diffutilrv.data

import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.get
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo

class DummyEmployeeDataUtilsTest {

    private lateinit var dataUtils: DummyEmployeeDataUtils

    @Before
    fun setUp() {
        dataUtils = DummyEmployeeDataUtils()
    }

    @Test
    fun getEmployeeListSortedByName() {
        expectThat(dataUtils.getEmployeeListSortedByName()).hasSize(9).and {
            get(0).and {
                get(Employee::name).isEqualTo("Employee 1")
                get(Employee::role).isEqualTo("Developer")
            }
            get(1).and {
                get(Employee::name).isEqualTo("Employee 2")
                get(Employee::role).isEqualTo("Tester")
            }
            get(2).and {
                get(Employee::name).isEqualTo("Employee 3")
                get(Employee::role).isEqualTo("Support")
            }
            get(3).and {
                get(Employee::name).isEqualTo("Employee 4")
                get(Employee::role).isEqualTo("Sales Manager")
            }
            get(4).and {
                get(Employee::name).isEqualTo("Employee 5")
                get(Employee::role).isEqualTo("Manager")
            }
            get(5).and {
                get(Employee::name).isEqualTo("Employee 6")
                get(Employee::role).isEqualTo("Team lead")
            }
            get(6).and {
                get(Employee::name).isEqualTo("Employee 7")
                get(Employee::role).isEqualTo("Scrum Master")
            }
            get(7).and {
                get(Employee::name).isEqualTo("Employee 8")
                get(Employee::role).isEqualTo("Sr. Tester")
            }
            get(8).and {
                get(Employee::name).isEqualTo("Employee 9")
                get(Employee::role).isEqualTo("Sr. Developer")
            }
        }
    }

    @Test
    fun getEmployeeListSortedByRole() {
        expectThat(dataUtils.getEmployeeListSortedByRole()).hasSize(9).and {
            get(0).and {
                get(Employee::name).isEqualTo("Employee 2")
                get(Employee::role).isEqualTo("Tester")
            }
            get(1).and {
                get(Employee::name).isEqualTo("Employee 6")
                get(Employee::role).isEqualTo("Team lead")
            }
            get(2).and {
                get(Employee::name).isEqualTo("Employee 3")
                get(Employee::role).isEqualTo("Support")
            }
            get(3).and {
                get(Employee::name).isEqualTo("Employee 8")
                get(Employee::role).isEqualTo("Sr. Tester")
            }
            get(4).and {
                get(Employee::name).isEqualTo("Employee 9")
                get(Employee::role).isEqualTo("Sr. Developer")
            }
            get(5).and {
                get(Employee::name).isEqualTo("Employee 7")
                get(Employee::role).isEqualTo("Scrum Master")
            }
            get(6).and {
                get(Employee::name).isEqualTo("Employee 4")
                get(Employee::role).isEqualTo("Sales Manager")
            }
            get(7).and {
                get(Employee::name).isEqualTo("Employee 5")
                get(Employee::role).isEqualTo("Manager")
            }
            get(8).and {
                get(Employee::name).isEqualTo("Employee 1")
                get(Employee::role).isEqualTo("Developer")
            }
        }
    }
}