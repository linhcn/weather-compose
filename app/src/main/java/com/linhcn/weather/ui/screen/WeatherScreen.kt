package com.linhcn.weather.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.linhcn.weather.R
import com.linhcn.weather.common.IndicatorScreen
import com.linhcn.weather.data.local.entities.Weather
import com.linhcn.weather.data.local.entities.getWeatherIconUrl
import com.linhcn.weather.models.isEmptyData
import com.linhcn.weather.ui.theme.Blue200
import com.linhcn.weather.ui.theme.Blue700
import com.linhcn.weather.ui.theme.Blue900
import com.linhcn.weather.ui.theme.WeatherTheme
import com.linhcn.weather.utils.*
import java.util.*

@Composable
fun WeatherScreen(navController: NavHostController, viewModel: WeatherViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = CenterHorizontally
    ) {
        DateInWeeks(list = viewModel.dateInWeeksState, onClick = {
            viewModel.onAction(WeatherAction.SelectedDate(it))
        })
        WeatherContent(viewModel.state)
    }
}

@Composable
fun WeatherContent(weatherState: WeatherState) {
    IndicatorScreen(isLoading = weatherState.isLoading) {
        var offset by remember {
            mutableStateOf(0f)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    orientation = Orientation.Vertical,
                    state = rememberScrollableState { delta ->
                        offset += delta
                        delta
                    }
                ),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (weatherState.isLoadingSuccess) {
                // TODO: update UI to swipe refresh
//                Text(text = offset.toString())

                val weather = weatherState.weather!!

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(weather.getWeatherIconUrl())
                        .crossfade(400)
                        .build(),
                    contentDescription = "Weather Image",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(20.dp),
                )
                val temp = weather.theTemp?.toInt()?.toString() ?: "0"
                Text(
                    text = buildAnnotatedString {
                        append(AnnotatedString("$temp°", spanStyle = SpanStyle(fontSize = 90.sp)))
                        append("c")
                    }, style = TextStyle(
                        color = Blue700,
                        fontSize = 70.sp
                    )
                )
                Text(
                    text = weather.stateName ?: "",
                    style = TextStyle(color = Blue200, fontSize = 30.sp)
                )
                Text(
                    text = weather.applicableDate?.getName() ?: "",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(20.dp)
                )
                Row {
                    WeatherPercent(
                        percentValue = weather.humidity ?: 0,
                        color = Blue200,
                        desc = stringResource(id = R.string.humidity),
                        modifier = Modifier.padding(5.dp)
                    )
                    WeatherPercent(
                        percentValue = weather.predictability ?: 0,
                        color = Blue900,
                        desc = stringResource(id = R.string.predictability),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            } else {
                val error = weatherState.error
                if (error.isEmptyData()) {
                    Text(
                        color = Color.Black,
                        text = stringResource(id = R.string.no_data),
                    )
                } else {
                    Snackbar(modifier = Modifier.padding(8.dp)) {
                        Text(
                            color = Color.Black,
                            text = error.message,
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun WeatherPercent(
    percentValue: Int,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(50.dp),
        modifier = modifier
            .width(100.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.padding(15.dp)
        ) {
            val humidity = percentValue.toFloat() / 100F
            PercentProcessBar(
                initialValue = humidity,
                inactiveBarColor = Color.LightGray,
                activeBarColor = Color.White,
                modifier = Modifier.size(70.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        30.dp
                    ),
                    color = Color.White,
                    modifier = Modifier
                        .size(55.dp)
                        .align(alignment = CenterHorizontally)
                ) {}
            }
            Text(
                text = buildAnnotatedString {
                    append(
                        AnnotatedString(
                            text = percentValue.toString(),
                            spanStyle = SpanStyle(fontSize = 30.sp)
                        )
                    )
                    append(text = "%")
                },
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                )
            )
            Text(
                text = desc,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun PercentProcessBar(
    modifier: Modifier = Modifier,
    initialValue: Float = 0f,
    inactiveBarColor: Color,
    activeBarColor: Color,
    strokeWith: Dp = 5.dp,
    content: @Composable () -> Unit
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val currentValue by remember {
        mutableStateOf(initialValue)
    }
    Box(
        modifier = modifier.onSizeChanged {
            size = it
        },
        contentAlignment = Center
    ) {
        content()
        Canvas(modifier = modifier) {
            drawArc(
                color = inactiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWith.toPx() / 2, cap = StrokeCap.Round)
            )
            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * currentValue,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWith.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Preview(showBackground = true, name = "Weather Percent")
@Composable
fun WeatherPercentPreview() {
    MaterialTheme {
        WeatherPercent(
            50,
            color = Blue200,
            desc = "Humidity"
        )
    }
}

@Composable
fun DateInWeek(
    state: DateInWeekState,
    modifier: Modifier,
    onClick: (state: DateInWeekState) -> Unit
) {
    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Blue700, if (state.isSelected) Blue200 else Blue700)
                )
            )
            .clickable {
                onClick(state.apply { isSelected = true })
            }
            .padding(top = 5.dp, start = 5.dp, end = 5.dp, bottom = 10.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        Text(text = state.date.getLetterDateOfWeek(), color = Color.White)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = state.date.getShortName(), color = Color.White)
    }

}

@Composable
fun DateInWeeks(list: List<DateInWeekState>, onClick: (state: DateInWeekState) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blue700),
    ) {
        for (i in list) {
            DateInWeek(state = i, modifier = Modifier.weight(1f), onClick)
        }
    }
}

@Preview(showBackground = true, name = "List Date In Week")
@Composable
fun DateInWeekPreview() {
    WeatherTheme {
        val dateInWeeksState = remember {
            mutableStateListOf<DateInWeekState>().apply {
                val dateInWeeks = Calendar.getInstance().getListDateOfWeek(0)
                for (d in dateInWeeks) {
                    add(DateInWeekState(d, d.isToday()))
                }
            }
        }
        DateInWeeks(dateInWeeksState) {
            it.isSelected = true
        }
    }
}

@Preview(showBackground = true, name = "Weather Content")
@Composable
fun WeatherContentPreview() {
    WeatherTheme {
        val weatherState by remember {
            mutableStateOf(
                WeatherState(
                    weather = Weather(id = 2309459340L, stateAbbr = "hr"),
                    isLoading = false,
                    isLoadingSuccess = true
                )
            )
        }
        WeatherContent(weatherState = weatherState)
    }
}
