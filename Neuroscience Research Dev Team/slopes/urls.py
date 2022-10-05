from django.urls import path

from slopes import views

urlpatterns = [
    path('', views.index, name='index'),
    path('<int:question_number_id>/', views.survey_test, name='survey_test'),

]