import pandas as pd
import numpy as np 
from sklearn.model_selection import train_test_split
from pycaret.regression import *


#Load the data

csv_path = "https://raw.githubusercontent.com/curiousily/Deep-Learning-For-Hackers/master/data/3.stock-prediction/BTC-USD.csv"
asset = pd.read_csv(csv_path, parse_dates=['Date'])
asset = asset.sort_values('Date')

print(asset)

future_days = 1

asset['Future_Price'] = asset[['Close']].shift(-future_days)

asset = asset[['Close', 'Future_Price', ]]

print(asset)
#df = dataframe
df = asset.copy() 

#Creating independant data set
X = np.array(df[df.columns])

#Remove last n rows from the dataset where n is future days
#in this case 1
X = X[:len(asset)-future_days]


#Create the dependant dataset
Y = np.array(df['Future_Price'])
#Get all y values except for the last n rows 
#which is future_days = 1
Y = Y[:-future_days]


#Make training and test data sets

x_train, x_test, y_train, y_test = train_test_split(X, Y, test_size=0.15, random_state= 0, shuffle=False)

# Train data -> data frame

train_data = pd.DataFrame(x_train, columns= df.columns)

#show the first 7 rows of data
print(train_data.tail(7))

#Same with test data 

test_data = pd.DataFrame(x_test, columns=df.columns)
print(test_data.tail(7))

regression_setup = setup(data=train_data, target= 'Future_Price', session_id=123, use_gpu=True)

# Train on all of the models and sort it by r-squared metric aka (r2) and then store the model with the best r2 score

best_model = compare_models(sort='r2')
# compares loads of models on my dataset
# r2 = statistical measure that represents the preportion of the variants for the dependant variable thats explained by an independant variables(s) in a regression model. 
# 


model = create_model(best_model) 

evaluate_model(model)

#Get the predictions
unseen_predictions = predict_model(model, data=test_data)
#Show the predictions 
print(unseen_predictions)

unseen_predictions.to_csv('test.csv')