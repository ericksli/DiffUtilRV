package com.example.diffutilrv.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diffutilrv.R
import com.example.diffutilrv.data.EmployeeSortBy
import com.example.diffutilrv.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerViewAdapter: EmployeeRecyclerViewAdapter
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerViewAdapter = EmployeeRecyclerViewAdapter(this, viewModel)
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = recyclerViewAdapter
        }
        observeLiveData()
        if (viewModel.employees.value.isEmpty()) {
            viewModel.loadData()
        }
    }

    private fun observeLiveData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.employees.collectLatest { recyclerViewAdapter.submitList(it) }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickRowAction.collectLatest {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.row_clicked_message, it.name),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clickRowButtonAction.collectLatest {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.row_button_clicked_message, it.name),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.sort_by_name -> {
            viewModel.changeSorting(EmployeeSortBy.NAME)
            true
        }
        R.id.sort_by_role -> {
            viewModel.changeSorting(EmployeeSortBy.ROLE)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
