package com.linhcn.weather.ui.screen

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.linhcn.weather.data.local.entities.Weather
import com.linhcn.weather.data.local.entities.getWeatherSuitableTime
import com.linhcn.weather.models.Error
import com.linhcn.weather.models.Result
import com.linhcn.weather.repo.WeatherRepository
import com.linhcn.weather.utils.getListDateOfWeek
import com.linhcn.weather.utils.isToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repo: WeatherRepository) : ViewModel() {

    private val pendingAction = MutableSharedFlow<WeatherAction>()

    val dateInWeeksState = mutableStateListOf<DateInWeekState>().apply {
        val dateInWeeks = Calendar.getInstance().getListDateOfWeek(0)
        for (d in dateInWeeks) {
            add(DateInWeekState(d, d.isToday()))
        }
    }
    var state by mutableStateOf(WeatherState())
        private set

    fun onAction(action: WeatherAction) {
        viewModelScope.launch {
            pendingAction.emit(action)
        }
    }

    private suspend fun handleActions() {
        pendingAction.collect() { ac ->
            when (ac) {
                is WeatherAction.SelectedDate -> {

                    state = state.copy(isLoading = true)

                    // update UI for selected date
                    val selectedIndex = dateInWeeksState.indexOf(ac.state)
                    for (item in dateInWeeksState) {
                        item.isSelected =
                            !(item != dateInWeeksState[selectedIndex] && item.isSelected)
                    }

                    // get weathers data on selected date
                    val res = repo.getWeathersOnDate(ac.state.date)
                    state = when (res) {
                        is Result.Success -> {
                            state.copy(
                                weather = res.data!!.getWeatherSuitableTime(),
                                isLoadingSuccess = true,
                                isLoading = false
                            )
                        }
                        is Result.Failure -> {
                            state.copy(
                                error = res.error,
                                isLoadingSuccess = false,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}


data class WeatherState(
    var weather: Weather? = null,
    var error: Error? = null,
    var isLoading: Boolean = false,
    var isLoadingSuccess: Boolean = false,
)

data class DateInWeekState(
    var date: Date = Date(),
    var isSelected: Boolean = false
)

sealed class WeatherAction {
    data class SelectedDate(val state: DateInWeekState) : WeatherAction()
}
