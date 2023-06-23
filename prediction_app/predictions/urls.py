from django.urls import path
from .views import latest_prediction, create_prediction, PredictionApiView, LatestPredictionApiView, get_prediction

urlpatterns = [
    path('latest-prediction/', latest_prediction, name='latest_prediction'),
    path('create-prediction/', create_prediction, name='create_prediction'),
    path('api/all', PredictionApiView.as_view()),
    path('api/latest', LatestPredictionApiView.as_view()),
    path('get/', get_prediction, name="get_prediction"),
]