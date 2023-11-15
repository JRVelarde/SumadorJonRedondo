package com.example.prueba1evaluacion_jon_redondo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prueba1evaluacion_jon_redondo.ui.theme.Prueba1ªEvaluacion_Jon_RedondoTheme
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prueba1ªEvaluacion_Jon_RedondoTheme {
                val navController = rememberNavController()
                val calculatorViewModel: CalculatorViewModel = viewModel()

                NavHost(navController, startDestination = "inputScreen") {
                    composable("inputScreen") {
                        InputScreen(navController, calculatorViewModel)
                    }
                    composable("resultScreen/{result}") { backStackEntry ->
                        val result = backStackEntry.arguments?.getString("result")?.toDoubleOrNull() ?: 0.0
                        ResultScreen(result, calculatorViewModel.history.value ?: emptyList()) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavController, calculatorViewModel: CalculatorViewModel) {
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Número 1") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        TextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Número 2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Button(
            onClick = {
                if (number1.isNotEmpty() && number2.isNotEmpty()) {
                    val sum = number1.toDouble() + number2.toDouble()
                    result = "Resultado: $sum"
                    val operation = "$number1 + $number2 = $sum"
                    calculatorViewModel.addToHistory(operation)
                    navController.navigate("resultScreen/$sum")
                } else {
                    result = "Por favor, ingrese ambos números."
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Sumar")
        }

        Text(
            text = result,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ResultScreen(result: Double, previousResults: List<String>, onBackClicked: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Resultado actual: $result")
        Text("Resultados anteriores:")
        if (previousResults.isNotEmpty()) {
            previousResults.forEach { operation ->
                Text(operation)
            }
        } else {
            Text("No hay resultados anteriores")
        }
        Button(
            onClick = { onBackClicked() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Volver")
        }
    }
}



class CalculatorViewModel : ViewModel() {
    private val _history = MutableLiveData<List<String>>(emptyList())
    val history: LiveData<List<String>> = _history

    fun addToHistory(operation: String) {
        val currentList = _history.value ?: emptyList()
        val updatedList = currentList.toMutableList()
        updatedList.add(operation)
        _history.value = updatedList
    }
}
