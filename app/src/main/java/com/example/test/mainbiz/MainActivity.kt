package com.example.test.mainbiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.common.ui.theme.TestAppTheme
import com.example.test.mainbiz.presentation.MainViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SampleMultilineTextWithViewModel()
                }
            }
        }
    }
}

@Composable
fun SampleMultilineTextWithViewModel(viewModel: MainViewModel = koinViewModel()) {
    val textContent = viewModel.textContent.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.padding(0.dp),
            onClick = {
                viewModel.onMyButtonClicked()
            }
        ) {
            Text("Do Action")
        }

        Text(
            text = textContent.value,
            modifier = Modifier
                .padding(0.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestAppTheme {
        SampleMultilineTextWithViewModel()
    }
}