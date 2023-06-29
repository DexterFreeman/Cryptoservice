from django.shortcuts import render, redirect
from .models import Prediction
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework import permissions
from .serializers import PredictionSerializer, PredictionCreateSerializer
import numpy as np
import datetime as dt
import yfinance as yf
import pandas as pd
from sklearn.preprocessing import MinMaxScaler
from keras.layers import Dense, Dropout, LSTM
from keras.models import Sequential
from decimal import Decimal
from django.http import JsonResponse
from .services import make_prediction


class PredictionApiView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request, *args, **kwargs):
        predictions = Prediction.objects
        serializer = PredictionSerializer(predictions, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)


def get_latest_prediction_by_currency(request, currency):
    try:
        prediction = Prediction.objects.filter(cryptocurrency=currency).latest(
            "timestamp"
        )
        prediction_data = {
            "currency": prediction.cryptocurrency,
            "predicted_price": str(prediction.predicted_price),
        }
        return JsonResponse(prediction_data)
    except Prediction.DoesNotExist:
        return JsonResponse({"error": "Prediction not found"}, status=404)


def get_prediction(request):
    serializer = PredictionCreateSerializer(data=request.GET)
    if serializer.is_valid():
        currency = serializer.validated_data["currency"]

        # Perform prediction logic here
        prediction = make_prediction(currency)

        # Create the Prediction object
        prediction_obj = Prediction(cryptocurrency=currency, predicted_price=prediction)
        prediction_obj.save()

        # Prepare the response
        response_data = {
            "currency": currency,
            "predicted_price": str(prediction),
        }

        return JsonResponse(response_data, status=200, content_type="application/json")
    else: 
        return JsonResponse(serializer.errors, status=400, content_type="application/json")

class LatestPredictionApiView(APIView):
    def get(self, request, *args, **kwargs):
        predictions = Prediction.objects.last()
        serializer = PredictionSerializer(predictions)
        return Response(serializer.data, status=status.HTTP_200_OK)


def latest_prediction(request):
    latest_prediction = Prediction.objects.last()
    return render(
        request, "predictions/latest_prediction.html", {"prediction": latest_prediction}
    )
