import json, urllib.request, uuid

def request(port,path,data=None):
    url=f'http://localhost:{port}{path}'
    req=urllib.request.Request(url,data=None if data is None else json.dumps(data).encode(),headers={'Content-Type':'application/json'})
    with urllib.request.urlopen(req,timeout=10) as response:
        result=json.load(response);print(response.status,url,json.dumps(result,ensure_ascii=False));return result

suffix=uuid.uuid4().hex[:8]
student=request(8091,'/api/students',{'studentNumber':suffix,'name':'金正宇','major':'计算机'})
course=request(8092,'/api/courses',{'code':suffix,'title':'微服务注册发现','capacity':2,
    'instructor':{'id':'t1','name':'teacher','email':'teacher@example.com'},
    'schedule':{'dayOfWeek':'MONDAY','startTime':'09:00','endTime':'10:00'}})
enrollment=request(8093,'/api/enrollments',{'studentId':student['id'],'courseId':course['id']})
assert enrollment['studentId']==student['id']
assert request(8092,'/api/courses/'+course['id'])['enrolled']==1
print('PASS: COURSE_GROUP student/course lookup and successful enrollment')
