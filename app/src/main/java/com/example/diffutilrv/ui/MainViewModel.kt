package com.example.diffutilrv.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diffutilrv.data.Employee
import com.example.diffutilrv.data.EmployeeSortBy
import com.example.diffutilrv.domain.GetEmployeeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal const val STATE_SORT_BY = "sortBy"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val getEmployeeUseCase: GetEmployeeUseCase,
) : ViewModel() {
    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees: StateFlow<List<Employee>> = _employees
    private val _clickRowAction = MutableSharedFlow<Employee>()
    val clickRowAction: SharedFlow<Employee> = _clickRowAction
    private val _clickRowButtonAction = MutableSharedFlow<Employee>()
    val clickRowButtonAction: SharedFlow<Employee> = _clickRowButtonAction

    fun loadData(sortBy: EmployeeSortBy? = null) {
        viewModelScope.launch {
            _employees.value = getEmployeeUseCase(sortBy ?: state[STATE_SORT_BY] ?: EmployeeSortBy.ROLE)
        }
    }

    fun changeSorting(sortBy: EmployeeSortBy) {
        state[STATE_SORT_BY] = sortBy
        loadData(sortBy)
    }

    fun onClickRow(employee: Employee) {
        viewModelScope.launch { _clickRowAction.emit(employee) }
    }

    fun onClickRowButton(employee: Employee) {
        viewModelScope.launch { _clickRowButtonAction.emit(employee) }
    }
}