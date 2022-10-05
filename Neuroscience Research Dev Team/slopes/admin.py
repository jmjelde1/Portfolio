from django.contrib import admin
from .models import Questions, response, QuestionOnSurvey, Survey, User, ExitSurvey, Email
# Register your models here.


class QuestionAdmin(admin.ModelAdmin):
    fields = ['pub_date', 'question_text']

admin.site.register(Questions)
admin.site.register(response)
admin.site.register(QuestionOnSurvey)
admin.site.register(Survey)
admin.site.register(User)
admin.site.register(ExitSurvey)
admin.site.register(Email)