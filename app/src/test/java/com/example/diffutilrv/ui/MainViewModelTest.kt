package com.example.diffutilrv.ui

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.diffutilrv.data.Employee
import com.example.diffutilrv.data.EmployeeSortBy
import com.example.diffutilrv.domain.GetEmployeeUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import kotlin.time.ExperimentalTime

@ExperimentalTime
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var getEmployeeUseCase: GetEmployeeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `loadData sort by null without saved state`() = runBlockingTest {
        instantiateMainViewModel()
        val employees = listOf(
            Employee(2, "Employee 2", "Tester"),
            Employee(1, "Employee 1", "Developer"),
        )
        coEvery { getEmployeeUseCase(EmployeeSortBy.ROLE) } returns employees
        viewModel.employees.test {
            viewModel.loadData()
            expectThat(expectItem()).isEqualTo(emptyList())
            expectThat(expectItem()).isEqualTo(employees)
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun `loadData sort by role without saved state`() = runBlockingTest {
        instantiateMainViewModel()
        val employees = emptyList<Employee>()
        coEvery { getEmployeeUseCase(EmployeeSortBy.ROLE) } returns employees
        viewModel.employees.test {
            viewModel.loadData(EmployeeSortBy.ROLE)
            expectThat(expectItem()).isEqualTo(emptyList())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadData sort by name without saved state`() = runBlockingTest {
        instantiateMainViewModel()
        val employees = emptyList<Employee>()
        coEvery { getEmployeeUseCase(EmployeeSortBy.NAME) } returns employees
        viewModel.employees.test {
            viewModel.loadData(EmployeeSortBy.NAME)
            expectThat(expectItem()).isEqualTo(emptyList())
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun `loadData sort by null with saved state sort by name`() = runBlockingTest {
        instantiateMainViewModel(EmployeeSortBy.NAME)
        val employees = emptyList<Employee>()
        coEvery { getEmployeeUseCase(EmployeeSortBy.NAME) } returns employees
        viewModel.employees.test {
            expectThat(expectItem()).isEqualTo(emptyList())
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun `loadData sort by role with saved state sort by name`() = runBlockingTest {
        instantiateMainViewModel(EmployeeSortBy.NAME)
        val employees = emptyList<Employee>()
        coEvery { getEmployeeUseCase(EmployeeSortBy.ROLE) } returns employees
        viewModel.employees.test {
            viewModel.loadData(EmployeeSortBy.ROLE)
            expectThat(expectItem()).isEqualTo(emptyList())
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun `loadData sort by name with saved state sort by role`() = runBlockingTest {
        instantiateMainViewModel(EmployeeSortBy.ROLE)
        val employees = emptyList<Employee>()
        coEvery { getEmployeeUseCase(EmployeeSortBy.NAME) } returns employees
        viewModel.employees.test {
            viewModel.loadData(EmployeeSortBy.NAME)
            expectThat(expectItem()).isEqualTo(emptyList())
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun onClickRow() = runBlockingTest {
        instantiateMainViewModel()
        viewModel.clickRowAction.test {
            val employee = Employee(8, "Employee 8", "Sr. Tester")
            viewModel.onClickRow(employee)
            expectThat(expectItem()).isEqualTo(employee)
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    @Test
    fun onClickRowButton() = runBlockingTest {
        instantiateMainViewModel()
        viewModel.clickRowButtonAction.test {
            val employee = Employee(6, "Employee 6", "Team lead")
            viewModel.onClickRowButton(employee)
            expectThat(expectItem()).isEqualTo(employee)
            expectThat(cancelAndConsumeRemainingEvents()).isEmpty()
        }
    }

    private fun instantiateMainViewModel(sortBy: EmployeeSortBy? = null) {
        val savedStateHandle = if (sortBy == null) {
            SavedStateHandle()
        } else {
            SavedStateHandle(mapOf(STATE_SORT_BY to sortBy))
        }
        viewModel = MainViewModel(savedStateHandle, getEmployeeUseCase)
    }
}