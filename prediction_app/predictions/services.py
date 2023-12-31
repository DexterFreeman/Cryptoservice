import numpy as np
import os
os.environ["CUDA_VISIBLE_DEVICES"] = "0" 
import tensorflow as tf

physical_devices = tf.config.list_physical_devices('GPU')
import datetime as dt
import yfinance as yf
import pandas as pd
from sklearn.preprocessing import MinMaxScaler
from keras.layers import Dense, Dropout, LSTM
from keras.models import Sequential

def create_model(x_train, y_train): 
    # Create the NN
    model = Sequential()
    model.add(LSTM(units=50, return_sequences=True, input_shape=(x_train.shape[1], 1)))
    model.add(Dropout(0.2))
    model.add(LSTM(units=50, return_sequences=True))
    model.add(Dropout(0.2))
    model.add(LSTM(units=50))
    model.add(Dropout(0.2))
    model.add(Dense(units=1))
    model.compile(optimizer="adam", loss="mean_squared_error")
    model.fit(x_train, y_train, epochs=25, batch_size=32)
    return model

def make_prediction(currency):
    
    crypto_currency = currency + "-USD"
    start = dt.datetime(2018, 1, 1)
    end = dt.datetime.now()

    # Get data from Yahoo Finance
    data = yf.download(crypto_currency, start=start, end=end)

    # Prepare and normalize data
    scaler = MinMaxScaler(feature_range=(0, 1))
    scaled_data = scaler.fit_transform(data["Close"].values.reshape(-1, 1))

    prediction_days = 60
    future_days = 7

    # Test and training data
    x_train, y_train = [], []

    for x in range(prediction_days, len(scaled_data) - future_days):
        x_train.append(scaled_data[x - prediction_days : x, 0])
        y_train.append(scaled_data[x + future_days, 0])

    x_train, y_train = np.array(x_train), np.array(y_train)
    x_train = np.reshape(x_train, (x_train.shape[0], x_train.shape[1], 1))

    model = create_model(x_train, y_train)

    # Testing the model
    test_start = dt.datetime(2020, 1, 1)
    test_end = dt.datetime.now()
    test_data = yf.download(crypto_currency, start=test_start, end=test_end)
    # actual_prices = test_data["Close"].values

    # Combining data
    total_dataset = pd.concat((data["Close"], test_data["Close"]), axis=0)
    model_inputs = total_dataset[
        len(total_dataset) - len(test_data) - prediction_days :
    ].values
    model_inputs = model_inputs.reshape(-1, 1)
    model_inputs = scaler.fit_transform(model_inputs)

    x_test = []
    for x in range(prediction_days, len(model_inputs)):
        x_test.append(model_inputs[x - prediction_days : x, 0])
    x_test = np.array(x_test)
    x_test = np.reshape(x_test, (x_test.shape[0], x_test.shape[1], 1))

    prediction_prices = model.predict(x_test)
    prediction_prices = scaler.inverse_transform(prediction_prices)

    # Predict next week
    real_data = [
        model_inputs[len(model_inputs) + 1 - prediction_days : len(model_inputs) + 1, 0]
    ]
    real_data = np.array(real_data)
    real_data = np.reshape(real_data, (real_data.shape[0], real_data.shape[1], 1))
    prediction = model.predict(real_data)
    prediction = scaler.inverse_transform(prediction)
    predicted_price = round(float(prediction[0][0]), 5)
    return predicted_price
