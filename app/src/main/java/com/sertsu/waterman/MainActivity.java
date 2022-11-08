package com.sertsu.waterman;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static String pass_string = "0AdSwE12NJHg8TI5UZ9YQO3KL7MbVfc";
    //  static String Url_Ext="sertsu.ddns.is74.ru:6874/?";
    //  static String Url_Int="192.168.0.222/?";
    static String Url = "sertsu.ddns.is74.ru:6874/?";
    //boolean requestFromArduino = false;
    String timeLastRequest = "";

    Snackbar myAwesomeSnackbar;

    Spinner degrPool;

    GridLayout irrigationScreen, poolScreen, saunaScreen;
    AppCompatSeekBar periodRequestWeather, timeIrrigationPeriodOneZone;

    Button saunaB, irrigationB, poolB, buttonBack, buttonRefresh;

    TextView realTimeIrrigationText, tempMax, periodRequestWeatherText, timeIrrigationPeriodOneZoneText,
            irrigatingNowText, lastRequestText, steamroomTemperatureText, restroomTemperatureText, washroomTemperatureText,
            outsideTemperatureText;
    ToggleButton acceptAutoIrrigationButton, irrigatingManualButton, irrigationValve1, irrigationValve2, irrigationValve3, irrigationValve4,
            openAllIrrigationValves, statePoolValveButton, stateHeaterValveButton;
    EditText timeIrrigationBeginMinute, timeIrrigationBeginHour;

    int tempMaxValue = 0, timeIrrigationPeriodOneZoneValue = 0, realTimeIrrigation = 0,
            periodRequestWeatherValue = 0, timeIrrigationBeginHourValue = 0,
            timeIrrigationBeginMinuteValue = 0, nowManualIrrigation = 0, acceptAutoIrrigation = 0,
            currentValveOpen = 0, poolTemperature = 0, hallSaunaTemperature = 0,
            steamroomTemperature = 0, restroomTemperature = 0, washroomTemperature = 0, outsideTemperature = 0,
            stateHeaterPool = 0, statePoolValve = 0, stateHoodWashRoom = 0, stateHoodSteamRoom = 0,
            stateFloorHeater = 0, stateConvector = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        irrigationScreen = findViewById(R.id.irrigation_screen);
        poolScreen = findViewById(R.id.pool_screen);
        saunaScreen = findViewById(R.id.sauna_screen);

        periodRequestWeather = findViewById(R.id.periodRequestWeather);
        timeIrrigationPeriodOneZone = findViewById(R.id.timeIrrigationPeriodOneZone);
        degrPool = findViewById(R.id.degrPool);

        saunaB = findViewById(R.id.saunaID);
        irrigationB = findViewById(R.id.irrigationID);
        poolB = findViewById(R.id.poolID);
        buttonBack = findViewById(R.id.buttonBack);
        buttonRefresh = findViewById(R.id.buttonRefresh);
        //  buttonSave = findViewById(R.id.buttonSave);

        tempMax = findViewById(R.id.tempMax);
        lastRequestText = findViewById(R.id.lastRequestText);
        periodRequestWeatherText = findViewById(R.id.periodRequestWeatherText);
        timeIrrigationPeriodOneZoneText = findViewById(R.id.timeIrrigationPeriodOneZoneText);
        irrigatingNowText = findViewById(R.id.irrigatingNowText);
        realTimeIrrigationText = findViewById(R.id.realTimeIrrigationText);

        acceptAutoIrrigationButton = findViewById(R.id.acceptAutoIrrigationButton);
        irrigatingManualButton = findViewById(R.id.irrigatingManualButton);
        irrigationValve1 = findViewById(R.id.irrigationValve1);
        irrigationValve2 = findViewById(R.id.irrigationValve2);
        irrigationValve3 = findViewById(R.id.irrigationValve3);
        irrigationValve4 = findViewById(R.id.irrigationValve4);
        openAllIrrigationValves = findViewById(R.id.openAllIrrigationValves);

        timeIrrigationBeginHour = findViewById(R.id.timeIrrigationBeginHour);
        timeIrrigationBeginMinute = findViewById(R.id.timeIrrigationBeginMinute);

        steamroomTemperatureText = findViewById(R.id.steamroomTemperatureText);
        restroomTemperatureText = findViewById(R.id.restroomTemperatureText);
        washroomTemperatureText = findViewById(R.id.washroomTemperatureText);
        outsideTemperatureText = findViewById(R.id.outsideTemperatureText);

        statePoolValveButton = findViewById(R.id.statePoolValveButton);
        stateHeaterValveButton = findViewById(R.id.stateHeaterValveButton);

        getInfoArduino();

        timeIrrigationPeriodOneZone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //int progressChanged = minimumValue;

            @SuppressLint("DefaultLocale")
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeIrrigationPeriodOneZoneText.setText(String.format("%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                timeIrrigationPeriodOneZoneText.setText(String.format("%d", seekBar.getProgress()));
                putCommandToArduino(17, 15, seekBar.getProgress());
            }
        });

        periodRequestWeather.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                periodRequestWeatherText.setText(String.format("%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                periodRequestWeatherText.setText(String.format("%d", seekBar.getProgress()));
                putCommandToArduino(17, 19, seekBar.getProgress());
            }
        });

        poolB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irrigationB.setVisibility(View.GONE);
                saunaB.setVisibility(View.GONE);
                poolB.setVisibility(View.GONE);
                poolScreen.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.VISIBLE);
                //       }
            }
        });

        saunaB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irrigationB.setVisibility(View.GONE);
                saunaB.setVisibility(View.GONE);
                poolB.setVisibility(View.GONE);
                saunaScreen.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.VISIBLE);
            }
        });

        irrigationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irrigationB.setVisibility(View.GONE);
                saunaB.setVisibility(View.GONE);
                poolB.setVisibility(View.GONE);
                irrigationScreen.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.VISIBLE);
            }
            //  }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irrigationB.setVisibility(View.VISIBLE);
                saunaB.setVisibility(View.VISIBLE);
                poolB.setVisibility(View.VISIBLE);
                irrigationScreen.setVisibility(View.GONE);
                poolScreen.setVisibility(View.GONE);
                saunaScreen.setVisibility(View.GONE);
                buttonBack.setVisibility(View.GONE);
                buttonRefresh.setVisibility(View.GONE);
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfoArduino();
            }
        });

        openAllIrrigationValves.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(3, 99, 1);
                    } else {
                        putCommandToArduino(3, 99, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        irrigationValve1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(3, 5, 1);
                    } else {
                        putCommandToArduino(3, 5, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        irrigationValve2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(3, 6, 1);
                    } else {
                        putCommandToArduino(3, 6, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        irrigationValve3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(3, 7, 1);
                    } else {
                        putCommandToArduino(3, 7, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        irrigationValve4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(3, 8, 1);
                    } else {
                        putCommandToArduino(3, 8, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        irrigatingManualButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(3, 12, 1);
                    } else {
                        putCommandToArduino(3, 12, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        acceptAutoIrrigationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(17, 23, 1);
                    } else {
                        putCommandToArduino(17, 23, 0);
                    }
                }
                //при любом раскладе возвращаем отображение предыдущего состояния
                //новое состояние установится после автозагрузки инфы с ардуино
            }
        });

        stateHeaterValveButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(4, 6, 1);
                    } else {
                        putCommandToArduino(4, 6, 0);
                    }
                }
            }
        });

        statePoolValveButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        putCommandToArduino(4, 7, 1);
                    } else {
                        putCommandToArduino(4, 7, 0);
                    }
                }
            }
        });

        timeIrrigationBeginHour.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                putCommandToArduino(17, 20, Integer.valueOf(textView.getText().toString()));
                return true;
            }
        });

        degrPool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] temp = getResources().getStringArray(R.array.degrPool);
                //Toast.makeText(MainActivity.this, temp[i], Toast.LENGTH_SHORT).show();
            }
        });

        timeIrrigationBeginMinute.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //Toast.makeText(MainActivity.this, ""+textView.getText(), Toast.LENGTH_SHORT).show();
                putCommandToArduino(17, 21, Integer.parseInt(textView.getText().toString()));
                return true;
            }
        });
    }

    private void getInfoArduino() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://" + Url
                        + pass_string.charAt(day) + pass_string.charAt(hour) + "+13+0+0",
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // 0 SET_INTERVAL_IRRIGATION_FOR_VALVE
                        // 1 SET_INTERVAL_TOGET_WEATHER_INFO
                        // 2 SET_HOUR_START_IRRIGATION
                        // 3 MINUTE_START_IRRIGATION
                        // 4 temp_max
                        // 5 current_open_irrigation_valve (99 когда открыты все)
                        // 6 need_irrigation
                        // 7 real_TIME_IRRIGATION_FOR_VALVE
                        // 8 timeLastRequest
                        // 9 accept irrigation
                        // 10 pool temperature
                        // 11 hall sauna temperature
                        // 12 steamroom sauna temperature
                        // 13 restroom sauna temperature
                        // 14 washroom sauna temperature
                        // 15 outside temperature
                        /* 16 state pump pool (on|off)*/ // not use
                        // 17 state heater pool (on|off) //нагрев бассейна
                        // 18 pool valve status (on|off) //принудительный долив бассейна
                        // 19 hood washroom state (on|off)
                        // 20 hood steamroom state (on|off)
                        // 21 floor heater state (on|off)
                        // 22 convector state (on|off)

                        String[] separated = response.replace('+', '_').split("_");
                        degrPool.setSelection(3);

                        if (separated.length > 0) {
                            timeIrrigationPeriodOneZoneValue = Integer.parseInt(separated[0]);
                        }
                        if (separated.length > 1) {
                            periodRequestWeatherValue = Integer.parseInt(separated[1]);
                        }
                        if (separated.length > 2) {
                            timeIrrigationBeginHourValue = Integer.parseInt(separated[2]);
                        }
                        if (separated.length > 3) {
                            timeIrrigationBeginMinuteValue = Integer.parseInt(separated[3]);
                        }
                        if (separated.length > 4) {
                            tempMaxValue = Integer.parseInt(separated[4]);
                        }
                        if (separated.length > 5) {
                            irrigationB.setText(getString(R.string.irrigation)
                                    + getString(R.string.air)
                                    + separated[4] + getString(R.string.tC));
                        }
                        if (separated.length > 6) {
                            currentValveOpen = Integer.parseInt(separated[5]);
                            //передавать номер реле
                        }
                        if (separated.length > 7) {
                            nowManualIrrigation = Integer.parseInt(separated[6]);
                            //активен ручной полив
                        }
                        if (separated.length > 8) {
                            realTimeIrrigation = Integer.parseInt(separated[7]) / 60;
                        }
                        if (separated.length > 9) {
                            timeLastRequest = separated[8];
                        }
                        if (separated.length > 10) {
                            acceptAutoIrrigation = Integer.parseInt(separated[9]);
                        }
                        if (separated.length > 11) {
                            poolTemperature = Integer.parseInt(separated[10]);
                        }
                        if (separated.length > 12) {
                            hallSaunaTemperature = Integer.parseInt(separated[11]);
                        }
                        if (separated.length > 13) {
                            steamroomTemperature = Integer.parseInt(separated[12]);
                            steamroomTemperatureText.setText(separated[12] + R.string.tC);
                        }
                        if (separated.length > 14) {
                            restroomTemperature = Integer.parseInt(separated[13]);
                            restroomTemperatureText.setText(separated[13] + R.string.tC);
                        }
                        if (separated.length > 15) {
                            washroomTemperature = Integer.parseInt(separated[14]);
                            washroomTemperatureText.setText(separated[14] + R.string.tC);
                        }
                        if (separated.length > 16) {
                            outsideTemperature = Integer.parseInt(separated[15]);
                            outsideTemperatureText.setText(separated[15] + R.string.tC);
                        }
                        //    if (separated.length > 17) {
                        //        statePumpPool = Integer.parseInt(separated[16]);
                        //        statePoolValveButton.setChecked(statePoolValve==1);
                        //
                        //    }
                        if (separated.length > 18) {
                            stateHeaterPool = Integer.parseInt(separated[17]);
                            stateHeaterValveButton.setChecked(stateHeaterPool == 1);
                        }
                        if (separated.length > 19) {
                            statePoolValve = Integer.parseInt(separated[18]);
                            statePoolValveButton.setChecked(statePoolValve == 1);
                        }
                        if (separated.length > 20) {
                            stateHoodWashRoom = Integer.parseInt(separated[19]);
                        }
                        if (separated.length > 21) {
                            stateHoodSteamRoom = Integer.parseInt(separated[20]);
                        }
                        if (separated.length > 22) {
                            stateFloorHeater = Integer.parseInt(separated[21]);
                        }
                        if (separated.length > 23) {
                            stateConvector = Integer.parseInt(separated[22]);
                        }

                        irrigatingManualButton.setChecked(nowManualIrrigation == 1); //выставляем на кнопке ВКЛ
                        irrigationValve1.setChecked(false);
                        irrigationValve2.setChecked(false);
                        irrigationValve3.setChecked(false);
                        irrigationValve4.setChecked(false);
                        openAllIrrigationValves.setChecked(false);
                        if (currentValveOpen != 0) { // какой-то клапан открыт, идёт полив
                            irrigatingNowText.setTextColor(Color.RED);
                            irrigatingNowText.setText("ИДЁТ ПОЛИВ");
                            switch (currentValveOpen) {//  выставляем статусы кнопок
                                case 5:
                                    irrigationValve1.setChecked(true);
                                    break;
                                case 6:
                                    irrigationValve2.setChecked(true);
                                    break;
                                case 7:
                                    irrigationValve3.setChecked(true);
                                    break;
                                case 8:
                                    irrigationValve4.setChecked(true);
                                    break;
                                case 99:
                                    irrigationValve1.setChecked(true);
                                    irrigationValve2.setChecked(true);
                                    irrigationValve3.setChecked(true);
                                    irrigationValve4.setChecked(true);
                                    openAllIrrigationValves.setChecked(true);
                                    break;
                            }
                        } else {
                            irrigatingNowText.setText("НЕ ПОЛИВАЕТ");
                            irrigatingNowText.setTextColor(Color.BLUE);
                        }

                        acceptAutoIrrigationButton.setChecked(acceptAutoIrrigation == 1);
                        tempMax.setText(String.format(Locale.getDefault(),
                                "%d" + getString(R.string.tC), tempMaxValue));
                        lastRequestText.setText(timeLastRequest);
                        timeIrrigationPeriodOneZone.setProgress(timeIrrigationPeriodOneZoneValue,
                                true);
                        timeIrrigationPeriodOneZoneText.setText(String.format(Locale.getDefault(),
                                "%d", timeIrrigationPeriodOneZoneValue));
                        periodRequestWeather.setProgress(periodRequestWeatherValue, true);
                        periodRequestWeatherText.setText(String.format(Locale.getDefault(),
                                "%d", periodRequestWeatherValue));
                        timeIrrigationBeginHour.setText(String.format(Locale.getDefault(),
                                timeIrrigationBeginHourValue < 10 ? "0%d" : "%d", timeIrrigationBeginHourValue));
                        timeIrrigationBeginMinute.setText(String.format(Locale.getDefault(),
                                timeIrrigationBeginMinuteValue < 10 ? "0%d" : "%d", timeIrrigationBeginMinuteValue));
                        if (realTimeIrrigation != 1) {//костыль, чтобы не выводить дефолтное состояние (шлёт ардуино) нужно исправить
                            realTimeIrrigationText.setText(String.format(Locale.getDefault(),
                                    "(%dмин)", realTimeIrrigation));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // queue.stop();
                irrigationB.setVisibility(View.VISIBLE);
                saunaB.setVisibility(View.VISIBLE);
                poolB.setVisibility(View.VISIBLE);
                irrigationScreen.setVisibility(View.GONE);
                poolScreen.setVisibility(View.GONE);
                saunaScreen.setVisibility(View.GONE);
                buttonRefresh.setVisibility(View.GONE);
                buttonBack.setVisibility(View.GONE);
                try {

                    if (error.networkResponse.statusCode == 403) {
                        myAwesomeSnackbar = Snackbar.make(
                                findViewById(R.id.rootLayout),
                                "Попробуйте ещё раз.    ",
                                Snackbar.LENGTH_SHORT
                        );
                    } else {
                        myAwesomeSnackbar = Snackbar.make(
                                findViewById(R.id.rootLayout),
                                "Нет связи с контроллером.",
                                Snackbar.LENGTH_SHORT
                        );
                    }
                } catch (Exception e) {
                    myAwesomeSnackbar = Snackbar.make(
                            findViewById(R.id.rootLayout),
                            "Что-то со связью или Интернета нет.",
                            Snackbar.LENGTH_SHORT);
                }
                View sbView = myAwesomeSnackbar.getView();
                TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextColor(Color.rgb(255, 100, 100));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                myAwesomeSnackbar.show();
            }
        });
        queue.add(stringRequest);
    }

    void putCommandToArduino(int G, int O, int A) {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        myAwesomeSnackbar = Snackbar.make(
                findViewById(R.id.rootLayout),
                "передаю настройки контроллеру",
                Snackbar.LENGTH_LONG
        );
        View sbView = myAwesomeSnackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.rgb(100, 100, 255));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        myAwesomeSnackbar.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://" + Url
                        + pass_string.charAt(day) + pass_string.charAt(hour) + "+" + G + "+" + O + "+" + A,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getInfoArduino();
                        if (G == 3 && O == 12) {//если действия по ручному поливу, делаем повторный запрос (чтобы не делать таймаут)
                            getInfoArduino();
                            getInfoArduino();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!error.toString().isEmpty()) {//сли есть ошибка, возвращаем состояние кнопок в исходное
                    if (G == 2) { //если это касается бани
                        //здесь откаты визуальных состояний по вентиляторам, теплому полу и т.д.
                    }
                    if (G == 3) { // если это касается полива
                        //здесь откаты визуальных состояний по клапанам
                        switch (O) {
                            case 5:
                                irrigationValve1.setChecked(!irrigationValve1.isChecked());
                                break;
                            case 6:
                                irrigationValve2.setChecked(!irrigationValve2.isChecked());
                                break;
                            case 7:
                                irrigationValve3.setChecked(!irrigationValve3.isChecked());
                                break;
                            case 8:
                                irrigationValve4.setChecked(!irrigationValve4.isChecked());
                                break;
                            case 99:
                                openAllIrrigationValves.setChecked(!openAllIrrigationValves.isChecked());
                                irrigationValve1.setChecked(openAllIrrigationValves.isChecked());
                                irrigationValve2.setChecked(openAllIrrigationValves.isChecked());
                                irrigationValve3.setChecked(openAllIrrigationValves.isChecked());
                                irrigationValve4.setChecked(openAllIrrigationValves.isChecked());
                                break;
                            case 12:
                                irrigatingManualButton.setChecked(!irrigatingManualButton.isChecked());
                                break;
                            case 23:
                                acceptAutoIrrigationButton.setChecked(acceptAutoIrrigationButton.isChecked());
                                break;
                        }
                    }
                    if (G == 4) { //если это касается бассейна
                        switch (O) { //здесь откаты визуальных состояний по клапану принудительного залива и нагреватели
                            case 6:
                                stateHeaterValveButton.setChecked(!stateHeaterValveButton.isChecked());
                                break;
                            case 7:
                                statePoolValveButton.setChecked(!statePoolValveButton.isChecked());
                                break;
                        }
                    }
                }
            }
        });
        queue.add(stringRequest);
    }
}
