"""Run from repository root: python scripts/verify-business.py [--outage]. Requires running Compose."""
import json, urllib.request, urllib.error, subprocess, time, sys
from decimal import Decimal

def request(port, path, method='GET', data=None, expected=200):
    url=f'http://localhost:{port}{path}'
    req=urllib.request.Request(url, data=None if data is None else json.dumps(data).encode(),
        headers={'Content-Type':'application/json'},method=method)
    try:
        with urllib.request.urlopen(req, timeout=12) as resp: status,body=resp.status,resp.read()
    except urllib.error.HTTPError as e: status,body=e.code,e.read()
    print(method,url,status,body.decode())
    assert status==expected,(status,expected)
    return json.loads(body) if body else None

customer=request(8081,'/api/customers','POST',{'name':'week07','phone':'13800000001'},201)
dish=request(8082,'/api/dishes','POST',{'name':'week07','category':'test','price':28,'available':True},201)
payload={'customerId':customer['id'],'items':[{'dishId':dish['id'],'quantity':2}]}
before=len(request(8083,'/api/orders'))
order=request(8083,'/api/orders','POST',payload,201)
assert Decimal(str(order['totalPrice']))==Decimal('56')
assert len(request(8083,'/api/orders'))==before+1
request(8083,f"/api/orders/{order['id']}/confirm",'POST',{},200)
request(8083,f"/api/orders/{order['id']}/complete",'POST',{},200)
before=len(request(8083,'/api/orders'))
request(8083,'/api/orders','POST',{'customerId':customer['id'],'items':[{'dishId':'missing','quantity':1}]},404)
assert len(request(8083,'/api/orders'))==before
if '--outage' in sys.argv:
    try:
        subprocess.run(['docker','compose','stop','menu-service'],check=True)
        time.sleep(15)
        request(8083,'/api/orders','POST',payload,503)
        assert len(request(8083,'/api/orders'))==before
    finally:
        subprocess.run(['docker','compose','start','menu-service'],check=True)
    for attempt in range(60):
        try:
            request(8082,'/actuator/health')
            # Allow discovery subscription to converge before retrying business.
            time.sleep(5)
            break
        except (OSError,AssertionError): time.sleep(2)
    request(8083,'/api/orders','POST',payload,201)
    assert len(request(8083,'/api/orders'))==before+1
print('PASS: business response and data-count assertions')
