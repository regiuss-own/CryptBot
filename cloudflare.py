import cloudscraper
import sys
import pyotp
import json

#cookies = {'session_token': 'ir92CEyoK1teKWon0QSWOs65UDPTLdY5nubjfduk'}

def Get2FA(token):
	return pyotp.TOTP(token).now()


def WaxWalletLogin(login, password, userToken2fa, captcha):
	scraper = cloudscraper.create_scraper()
	data = {
		"password": password,
		"username": login,
		"g-recaptcha-response": captcha,
		"redirectTo": ""
	}

	response = json.loads(scraper.post("https://all-access.wax.io/api/session", data).text)

	print(response)

	data={
		"code": Get2FA(userToken2fa),
		"token2fa": response["token2fa"]
	}

	response = scraper.post("https://all-access.wax.io/api/session/2fa", data)
	print(response.text)
	response = scraper.get("https://all-access.wax.io/api/session")
	print(response.text)
	result = {"token": json.loads(response.text)["token"]}
	print(result);


def WaxWalletResources(name):
	scraper = cloudscraper.create_scraper()
	data = {
		"account_name": name
	}

	response = json.loads(scraper.post("https://chain.wax.io/v1/chain/get_account", json.dumps(data)).text)

	print(response.get("cpu_limit"))


def GetUserInfo(token):
	scraper = cloudscraper.create_scraper()
	print(token)
	cookies = {'session_token': token}
	print(scraper.get("https://api-idm.wax.io/v1/accounts/auto-accept/login", cookies=cookies, headers={"origin":"https://play.alienworlds.io"}).text)


def Test():
	scraper = cloudscraper.create_scraper()
	print(scraper.get("https://all-access.wax.io/").text)	


def GetSignatures(transaction, captcha, token):
	transaction = eval(transaction)
	print("transaction: ", transaction)
	print("captcha: ", captcha)
	print("token: ", token)
	scraper = cloudscraper.create_scraper()

	print(scraper.options("https://public-wax-on.wax.io/wam/sign", headers={"origin":"https://all-access.wax.io"}, cookies={'session_token': token}))

	data={
        "description":"jwt is insecure",
        "g-recaptcha-response": captcha,
        "serializedTransaction": transaction,
        "website": "play.alienworlds.io"
	}
	print(json.dumps(data))
	response = scraper.post(
		"https://public-wax-on.wax.io/wam/sign",
		json.dumps(data),
		headers={"origin":"https://all-access.wax.io", "x-access-token": token, "content-type": "application/json;charset=UTF-8"}
	)
	print(response.text)


if(sys.argv[1] == "WaxWalletLogin"):
	WaxWalletLogin(sys.argv[2], sys.argv[3], sys.argv[4], sys.argv[5]);
elif(sys.argv[1] == "GetUserInfo"):
	GetUserInfo(sys.argv[2])
elif(sys.argv[1] == "GetSignatures"):
	GetSignatures(sys.argv[2], sys.argv[3], sys.argv[4]);
elif(sys.argv[1] == "Test"):
	Test();	
elif(sys.argv[1] == "Get2FA"):
	print(Get2FA(sys.argv[2]));	
elif(sys.argv[1] == "WaxWalletResources"):
	WaxWalletResources(sys.argv[2]);		