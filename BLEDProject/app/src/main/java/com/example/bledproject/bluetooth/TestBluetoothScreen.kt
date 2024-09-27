package com.example.bledproject.bluetooth

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun TestBluetoothScreen(bluetoothViewModel: BluetoothViewModel) {

	// уведомление при изменении connected
	LaunchedEffect(
		key1 = bluetoothViewModel.connected.value,
		block = {
			if (bluetoothViewModel.connected.value) {
				Toast.makeText(
					bluetoothViewModel.context,
					"Connected to ${bluetoothViewModel.connectedDevice.value}",
					Toast.LENGTH_SHORT
				).show()
			} else {
				Toast.makeText(
					bluetoothViewModel.context,
					"Disconnected",
					Toast.LENGTH_SHORT
				).show()
			}
		}
	)

	Column {
		Row {
			Button(
				modifier = Modifier,
				onClick = {
					if (bluetoothViewModel.scanning.value) {
						bluetoothViewModel.stopScan()
					} else {
						bluetoothViewModel.startScan()
					}
				}) {
				if (bluetoothViewModel.scanning.value) {
					Text("Stop Scan")
				} else {
					Text("Start Scan")
				}
			}
			if (bluetoothViewModel.connected.value) {
				Button(
					modifier = Modifier.fillMaxWidth(),
					onClick = {
						bluetoothViewModel.disconnect()
					}) {
					Text("Disconnect")
				}
			}
		}

		LazyColumn(modifier = Modifier.weight(1f)) {
			bluetoothViewModel.devices.forEach { device ->
				item {
					Row {
						if (ActivityCompat.checkSelfPermission(
								bluetoothViewModel.context,
								Manifest.permission.BLUETOOTH_CONNECT
							) != PackageManager.PERMISSION_GRANTED
						) {
							return@item
						}
						Text(device.name ?: "Unnamed device")
						Text(
							modifier = Modifier.weight(1f),
							text = device.address
						)
						Button(onClick = {
							bluetoothViewModel.connectToDevice(device)
						}) {
							Text("Connect")
						}
					}
				}
			}
		}

		if (bluetoothViewModel.connected.value) {
			Row {
				Text("Connected to: ")
				Text(text = bluetoothViewModel.connectedDevice.value)
			}
			Row {
				Text("Read Characteristic: ")
				Text(text = bluetoothViewModel.receivedData.value)
			}
			RotatingSquareScreen(bluetoothViewModel)
		}
	}

}

@Composable
fun RotatingSquareScreen(bluetoothViewModel: BluetoothViewModel) {
	var rotationX by remember { mutableStateOf(0f) }
	var rotationY by remember { mutableStateOf(0f) }

	LaunchedEffect(Unit) {
		while (true) {
			var data = bluetoothViewModel.receivedData.value;
			var (x, y) = parseRotationData(data)

			rotationX = x
			rotationY = y

			delay(50)
		}
	}
	RotatingColoredSquare(rotationXValue = rotationX, rotationYValue = rotationY)
}

@Composable
fun RotatingColoredSquare(rotationXValue: Float, rotationYValue: Float) {
	var rotationX by remember { mutableStateOf(rotationXValue) }
	var rotationY by remember { mutableStateOf(rotationYValue) }

	LaunchedEffect(rotationXValue, rotationYValue) {
		rotationX = rotationXValue
		rotationY = rotationYValue
	}

	// 4 примыкающих квадрата
	Canvas(
		modifier = Modifier
			.fillMaxWidth()
			.height(600.dp)
			.graphicsLayer(
				rotationX = rotationX,
				rotationY = rotationY
			)
	) {
		val squareSize = 100f

		val centerOffset = Offset(center.x - squareSize, center.y - squareSize)

		drawRect(
			color = Color.Red,
			topLeft = centerOffset,
			size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
		)

		drawRect(
			color = Color.Green,
			topLeft = Offset(centerOffset.x + squareSize, centerOffset.y),
			size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
		)

		drawRect(
			color = Color.Blue,
			topLeft = Offset(centerOffset.x, centerOffset.y + squareSize),
			size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
		)

		drawRect(
			color = Color.Yellow,
			topLeft = Offset(centerOffset.x + squareSize, centerOffset.y + squareSize),
			size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
		)
	}
}

fun parseRotationData(data: String): Pair<Float, Float> {
	return try {
		val parts = data.split(" | ")
		if (parts.size == 2) {
			val rotationX = parts[0].toFloatOrNull()?.coerceIn(-180f, 180f) ?: 0f
			val rotationY = parts[1].toFloatOrNull()?.coerceIn(-180f, 180f) ?: 0f

			//val roundedRotationX = String.format("%.1f", rotationX).toFloat()
			//val roundedRotationY = String.format("%.1f", rotationY).toFloat()

			rotationX to rotationY
		} else {
			0f to 0f
		}
	} catch (e: Exception) {
		0f to 0f
	}
}

fun generateRandomData(): String {
	val rotationX = Random.nextFloat() * 360 - 180
	val rotationY = Random.nextFloat() * 360 - 180
	return "$rotationX | $rotationY"
}


@Composable
fun RotatingSquare(rotationXValue: Float, rotationYValue: Float) {
	var rotationX by remember { mutableStateOf(rotationXValue) }
	var rotationY by remember { mutableStateOf(rotationYValue) }

	LaunchedEffect(rotationXValue, rotationYValue) {
		rotationX = rotationXValue
		rotationY = rotationYValue
	}

	Canvas(
		modifier = Modifier
			.fillMaxWidth()
			.height(800.dp)
			.graphicsLayer(
				rotationX = rotationX,
				rotationY = rotationY
			)
	) {
		drawRoundRect(
			color = Color.Blue,
			topLeft = Offset(center.x - 50f, center.y - 50f),
			size = androidx.compose.ui.geometry.Size(200f, 200f),
			cornerRadius = CornerRadius(10f)
		)
	}
}
