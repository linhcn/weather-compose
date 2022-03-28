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
import kotlinx.coroutines.flow.collect
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

    init {
        viewModelScope.launch {
            handleActions()
        }
        onAction(WeatherAction.SelectedDate(getSelectedState()))
    }

    private fun getSelectedState(): DateInWeekState {
        var selectedState: DateInWeekState? = null
        for (item in dateInWeeksState) {
            if (item.isSelected) {
                selectedState = item
            } else if (selectedState == null && item.date.isToday()) {
                selectedState = item
            }
        }
        return selectedState ?: dateInWeeksState[0]
    }

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
                        if (item != dateInWeeksState[selectedIndex]) {
                            if (item.isSelected) {
                                item.isSelected = false
                            }
                        }
                    }

                    // get weathers data on selected date
                    repo.getWeathersOnDate(ac.state.date).collect { res ->
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
}


data class WeatherState(
    var weather: Weather? = null,
    var error: Error = Error.unknownError(),
    var isLoading: Boolean = false,
    var isLoadingSuccess: Boolean = false,
)

data class DateInWeekState(
    var date: Date = Date().apply {
        time = (time / 10000L) * 10000L
    },
    var isSelected: Boolean = false
)

sealed class WeatherAction {
    data class SelectedDate(val state: DateInWeekState) : WeatherAction()
}
