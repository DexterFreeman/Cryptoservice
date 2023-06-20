from django.db import models

class Prediction(models.Model):
    timestamp = models.DateTimeField(auto_now_add=True)
    cryptocurrency = models.CharField(max_length=50)
    predicted_price = models.DecimalField(max_digits=10, decimal_places=2)

    def __str__(self):
        return f'{self.cryptocurrency}: {self.predicted_price}'