from rest_framework import serializers
from .models import Prediction

class PredictionCreateSerializer(serializers.Serializer):
    currency = serializers.CharField(max_length=50)

class PredictionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Prediction
        fields = ['timestamp', 'cryptocurrency', 'predicted_price']