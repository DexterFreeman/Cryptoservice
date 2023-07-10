# 3 Main parts: 

## React app: 

Front-facing application for the predictions, which features two pages. One showing a list of all the coins with a search bar to search for specific coins and then an individual page for each individual coin containing the market data, a chart of recent price changes and lastly a button to get a new prediction of the future price in 7 days which is then displayed as text.

## Django REST API: 

A rest API that is used to get predictions on the price in the next 7 days of cryptocurrencies given the currency name/symbol. To do so it uses a LSTM neural network using the python tensorflow package and market data on the currency from the year 2020 to bulid a model on predicting the price in the next 7 days. It then stores all of these predictions within SQLite to be accessed later if needed. There is additioanlly an endpoint for getting the latest prediction made, as well as the latest prediction made by currency. 


## Discord Bot:

I created a discord bot in java using the Discord JPA. This bot gives out messages/notfication everyday on the predicted price of the top 10 market cap currencies, with the current price and whether to buy or sell each currency. This task can also be called manually if a user wants to see a new set of predictions at a different time. It can also be used to create and display new predictions from the Django API
