from django.http import HttpResponse
from django.shortcuts import render, get_object_or_404, redirect
from django.views import generic
from slopes.models import *
import random
from random import choice
from os.path import join as path_join
from os import listdir
from os.path import isfile
import csv
# def index(request):
#     return HttpResponse("Welcome to the home page")

def index(request):
    context = {

    }

    return render(request, 'home.html', context)


def contact(request):
    context = {

    }

    return render(request, 'contact.html', context)


def about(request):
    context = {

    }

    return render(request, 'about.html', context)

def csv_file(request):
    # Create the HttpResponse object with the appropriate CSV header.
    response = HttpResponse(
        content_type='text/csv',
        headers={'Content-Disposition': 'attachment; filename="surveyResponses.csv"'},
    )

    writer = csv.writer(response)
    writer.writerow(['User', 'Question', 'Location', 'View Angle', 'Response', 'Correct answer'])
    #writer.writerow(['Second row', 'A', 'B', 'C', '"Testing"', "Here's a quote"])

    for survey in Survey.objects.all():


        survey_id = survey.id
        #new.append(survey_id)
        for questionsOnSurvey in QuestionOnSurvey.objects.filter(survey=survey):
            new = []
            questionObj = questionsOnSurvey.question
            surveyObj = questionsOnSurvey.survey
            number = questionsOnSurvey.number
            user_response = questionsOnSurvey.response
            #survey_id = survey.id
            new.append(survey_id)
            new.append(questionObj.image_name)
            new.append(questionObj.location)
            new.append(questionObj.view_angle)
            new.append(user_response)
            new.append(questionObj.actual)
            writer.writerow(new)

    return response

def csv_demographics(request):
    response = HttpResponse(
        content_type='text/csv',
        headers={'Content-Disposition': 'attachment; filename="userDemographics.csv"'},
    )

    writer = csv.writer(response)
    writer.writerow(['User', 'Age', 'Gender', 'Zipcode', 'Years of experience', 'Average Days', 'Activity'])

    for user in User.objects.all():
        exit = user.exitSurvey
        new = []
        new.append(user.id)
        new.append(exit.age)
        new.append(exit.gender)
        new.append(exit.zipcode)
        new.append(exit.experience)
        new.append(exit.averageDays)
        new.append(exit.activity)

        writer.writerow(new)

    return response







def csv_user_results(request):
    response = HttpResponse(
        content_type='text/csv',
        headers={'Content-Disposition': 'attachment; filename="Your_results.csv"'},
    )

    writer = csv.writer(response)
    writer.writerow(['Image Name', 'Location of image', 'View angle', 'Actual Slope', 'Your answer', 'Error'])

    survey_id = request.session.get('survey_id')
    survey = Survey.objects.get(id=survey_id)

    for questionsOnSurvey in QuestionOnSurvey.objects.filter(survey=survey):
        new = []
        image_name = questionsOnSurvey.question.image_name
        location = questionsOnSurvey.question.location
        view_angle = questionsOnSurvey.question.view_angle
        actual_slope = questionsOnSurvey.question.actual
        answer = questionsOnSurvey.response
        error = abs(answer - actual_slope)
        new.append(image_name)
        new.append(location)
        new.append(view_angle)
        new.append(actual_slope)
        new.append(answer)
        new.append(error)
        writer.writerow(new)

    return response


def csv_emails(request):
    response = HttpResponse(
        content_type='text/csv',
        headers={'Content-Disposition': 'attachment; filename="raffleEmails.csv"'},
    )
    writer = csv.writer(response)
    writer.writerow(['Number', 'Email'])

    for email in Email.objects.all():
        number = email.id
        e_mail = email.email

        new = []
        new.append(number)
        new.append(e_mail)

        writer.writerow(new)

    return response


def survey(request, question_id):
    '''data = Questions.objects.all()
    urls = Questions()
    urls.image_url()
    randInt = random.randint(1,3)
    q = {
        "question_number" : data,
        'urls': urls
    }
    return render(request, 'survey.html', q)'''
    '''
    context = {
        'question': get_object_or_404(Questions, pk=question_id)
    }'''

    # get survey id through sessions
    survey_id = request.session.get('survey_id')
    survey = Survey.objects.get(id = survey_id)

    #survey = Survey.objects.latest('id')
    # print('latest id', survey.id)
    # print('session id', survey_id)

    # question = Questions.objects.latest('id')
    # id = QuestionOnSurvey.objects.latest('id')
    print(survey)
    questions = list(QuestionOnSurvey.objects.filter(survey=survey, response=None))
    print("Len b4 pop", len(questions))
    print(questions)
    if questions:
        question = questions[0]
    else:
        question = None

    # to send the percentage of questions completed to survey.html
    len_all_questions = len(Questions.objects.all())
    len_questions_rem = len(questions)
    if (len_all_questions - len_questions_rem) != 0:
        bar_number = (len_all_questions - len_questions_rem) / len_all_questions * 100
    else:
        if len_all_questions == 1:
            bar_number = 100
        else:
            bar_number = 2
    # end

    if request.method == 'POST':
        # id = QuestionOnSurvey.objects.latest('id')
        if questions:
            print("Len b4 pop", len(questions))
            userSlopeValue = request.POST.get('slopeHidden')
            question.response = userSlopeValue
            question.save()
            #unnga a vise foprste bilde to ganger
            return redirect('survey', question_id = 17)
        else:
            print('some')
            return redirect('exit_survey')
    # QuestionOnSurvey.objects.filter(id=id).delete()
    # questions.pop()
    # questions.save()
    if questions:
        context = {
            'numbers': list(Questions.objects.all()),
            'questions': questions,
            # 'shuffled' : testList,
            'barNumber': bar_number,
            'quest': question.question
        }
    else:
        return redirect('exit_survey')
    return render(request, 'survey.html', context)

def take_survey(request):
    context = {

    }

    return render(request, 'take_survey.html', context)


def safety_info(request):
    context = {

    }

    return render(request, 'safety_info.html', context)


def exit_survey(request):

    # test2 = request.session.get('test')
    # print('sessionsTest', test2)
   # exit = ExitSurvey.objects.latest('id')

    # get survey_id through sessions
    survey_id = request.session.get('survey_id')
    exit =ExitSurvey.objects.get(id = survey_id)


    exit.email = request.POST.get('gender')

    # survey = Survey.objects.latest('id')

    # survey = Survey.objects.latest('id')
    # questions = list(QuestionOnSurvey.objects.filter(survey=survey))

    # for q in questions:
    #     q.


    if request.method == 'POST':

        exit.age = request.POST.get('age')
        exit.gender = request.POST.get('gender')
        exit.zipcode = request.POST.get('zipcode')
        exit.experience = request.POST.get('years_experience')
        exit.averageDays = request.POST.get('averagedays')
        exit.activity = request.POST.get('activities')

        exit.save()

        return redirect('survey_thanks')

    context = {

    }

    return render(request, 'exit_survey.html', context)


def survey_welcome(request):
    # test = 1
    # request.session['test'] = test + 1

    if request.method == 'POST':
        s = Survey()

        #request.session['survey_id'] = survey_id

        e = ExitSurvey()
        e.save()
        s.save()

        survey_id = s.id
        print('survey welcome s_id', s.id)

        # initialize survey_id and pass it through sessions
        request.session['survey_id'] = s.id

        user = User(survey=s, exitSurvey = e)
        user.save()
        print('new user', user.id)
        testList = list(Questions.objects.all())
        random.shuffle(testList)
        tableList = list(QuestionOnSurvey.objects.all())


        for q in range(0, len(testList)):
            db = QuestionOnSurvey(number=q, question=testList[q], survey=s, response=None)
            db.save()

        return redirect('survey', 17)

    context = {

    }
    return render(request, 'survey_welcome.html', context)

def survey_thanks(request):
    survey_id = request.session.get('survey_id')
    survey = Survey.objects.get(id = survey_id)
    questions = list(QuestionOnSurvey.objects.filter(survey=survey))
    print(questions)
    accumulated_diff = 0
    num_questions = 0
    for q in questions:
        num_questions += 1
        accumulated_diff += abs(q.response - q.question.actual)

        respone = q.response
        print(respone)

        print('question', q.question.actual)
        print(q)

    user_error = accumulated_diff / num_questions

    if request.method == 'POST':
        emailUser = request.POST.get('email')

        if emailUser == "":
            return redirect('index')
        else:
            email = Email()
            email.email = emailUser
            email.save()
            return redirect('index')


    context = {

        'user_error' : user_error,

    }


    return render(request, 'survey_thanks.html', context)


def postSurveyAnswer(request):
    context = {

    }
    if request.method == "POST":
        getAnswer = request.POST.get('slopeHidden')
        slopeAnswer = response(slopeGuess=getAnswer)
        slopeAnswer.save()
        postSurveyAnswer.has_been_called = True;
    else:
        postSurveyAnswer.has_been_called = False;

    return render(request, 'survey.html', context)

def errorCalc(request):
    context = {

    }
    if request.method == "POST":
        getAnswer = request.POST.get('slopeHidden')
        slopeAnswer = response(slopeGuess=getAnswer)



# def getImages(request):
#     context = {
#
#     }
#     imList = Questions.image
#     if postSurveyAnswer().has_been_called == True:
#         random_image = random.choice(imList.image)
#         return random_image


# def random_img():
#     dir_path = 'static'
#     files = [content for content in listdir(dir_path) if isfile(path_join(dir_path, content))]
#     return path_join(dir_path, choice(files))


def survey_test(request, question_id):
    context = {
        'question': get_object_or_404(Questions, pk=question_id)
    }
    return render(request, 'survey_test.html', context)


def admin_home(request):
    context = {

    }

    return render(request, 'admin_home.html', context)



