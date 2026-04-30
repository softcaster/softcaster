#----------------------------------------------------------------------
# Daycount
#----------------------------------------------------------------------
class Daycount:
    def __init__(self):
        __idDaycount = None
        __code = None
        __description = None

    def getIdDaycount(self):
        return self.__idDaycount
    def getCode(self):
        return self.__code
    def getDescription(self):
        return self.__description

    def setIdDaycount(self,value):
        self.__idDaycount=value
    def setCode(self,value):
        self.__code=value
    def setDescription(self,value):
        self.__description=value

    def fromJson(self,idDaycount,code,description):
        self.__idDaycount = idDaycount
        self.__code = code
        self.__description = description

    def toJson(self):
        return {"idDaycount":self.__idDaycount,"code":self.__code,"description":self.__description}

#----------------------------------------------------------------------
# Daycount
#----------------------------------------------------------------------
class Daycount:
    def __init__(self):
        __idDaycount = None
        __code = None
        __description = None

    def getIdDaycount(self):
        return self.__idDaycount
    def getCode(self):
        return self.__code
    def getDescription(self):
        return self.__description

    def setIdDaycount(self,value):
        self.__idDaycount=value
    def setCode(self,value):
        self.__code=value
    def setDescription(self,value):
        self.__description=value

    def fromJson(self,idDaycount,code,description):
        self.__idDaycount = idDaycount
        self.__code = code
        self.__description = description

    def toJson(self):
        return {"idDaycount":self.__idDaycount,"code":self.__code,"description":self.__description}

#----------------------------------------------------------------------
# Calendar
#----------------------------------------------------------------------
class Calendar:
    def __init__(self):
        __idCalendar = None
        __code = None
        __description = None

    def getIdCalendar(self):
        return self.__idCalendar
    def getCode(self):
        return self.__code
    def getDescription(self):
        return self.__description

    def setIdCalendar(self,value):
        self.__idCalendar=value
    def setCode(self,value):
        self.__code=value
    def setDescription(self,value):
        self.__description=value

    def fromJson(self,idCalendar,code,description):
        self.__idCalendar = idCalendar
        self.__code = code
        self.__description = description

    def toJson(self):
        return {"idCalendar":self.__idCalendar,"code":self.__code,"description":self.__description}

#----------------------------------------------------------------------
# Holiday
#----------------------------------------------------------------------
class Holiday:
    def __init__(self):
        __idHoliday = None
        __calendar = None
        __holidayDay = None
        __holidayMonth = None
        __description = None

    def getIdHoliday(self):
        return self.__idHoliday
    def getCalendar(self):
        return self.__calendar
    def getHolidayDay(self):
        return self.__holidayDay
    def getHolidayMonth(self):
        return self.__holidayMonth
    def getDescription(self):
        return self.__description

    def setIdHoliday(self,value):
        self.__idHoliday=value
    def setCalendar(self,value):
        self.__calendar=value
    def setHolidayDay(self,value):
        self.__holidayDay=value
    def setHolidayMonth(self,value):
        self.__holidayMonth=value
    def setDescription(self,value):
        self.__description=value

    def fromJson(self,idHoliday,calendar,holidayDay,holidayMonth,description):
        self.__idHoliday = idHoliday
        self.__calendar = Calendar()
        if(calendar != None):
            self.__calendar.fromJson(**calendar)
        self.__holidayDay = holidayDay
        self.__holidayMonth = holidayMonth
        self.__description = description

    def toJson(self):
        return {"idHoliday":self.__idHoliday,"calendar":self.__calendar.toJson(),"holidayDay":self.__holidayDay,"holidayMonth":self.__holidayMonth,"description":self.__description}

#----------------------------------------------------------------------
# Currency
#----------------------------------------------------------------------
class Currency:
    def __init__(self):
        __idCurrency = None
        __isoCode = None
        __currencyNumericCode = None
        __description = None
        __minorUnit = None
        __systemCurr = None
        __physicalCurr = None
        __calendar = None
        __businessDays = None
        __daycount = None

    def getIdCurrency(self):
        return self.__idCurrency
    def getIsoCode(self):
        return self.__isoCode
    def getCurrencyNumericCode(self):
        return self.__currencyNumericCode
    def getDescription(self):
        return self.__description
    def getMinorUnit(self):
        return self.__minorUnit
    def getSystemCurr(self):
        return self.__systemCurr
    def getPhysicalCurr(self):
        return self.__physicalCurr
    def getCalendar(self):
        return self.__calendar
    def getBusinessDays(self):
        return self.__businessDays
    def getDaycount(self):
        return self.__daycount

    def setIdCurrency(self,value):
        self.__idCurrency=value
    def setIsoCode(self,value):
        self.__isoCode=value
    def setCurrencyNumericCode(self,value):
        self.__currencyNumericCode=value
    def setDescription(self,value):
        self.__description=value
    def setMinorUnit(self,value):
        self.__minorUnit=value
    def setSystemCurr(self,value):
        self.__systemCurr=value
    def setPhysicalCurr(self,value):
        self.__physicalCurr=value
    def setCalendar(self,value):
        self.__calendar=value
    def setBusinessDays(self,value):
        self.__businessDays=value
    def setDaycount(self,value):
        self.__daycount=value

    def fromJson(self,idCurrency,isoCode,currencyNumericCode,description,minorUnit,systemCurr,physicalCurr,calendar,businessDays,daycount):
        self.__idCurrency = idCurrency
        self.__isoCode = isoCode
        self.__currencyNumericCode = currencyNumericCode
        self.__description = description
        self.__minorUnit = minorUnit
        self.__systemCurr = systemCurr
        self.__physicalCurr = physicalCurr
        self.__calendar = Calendar()
        if(calendar != None):
            self.__calendar.fromJson(**calendar)
        self.__businessDays = businessDays
        self.__daycount = Daycount()
        if(daycount != None):
            self.__daycount.fromJson(**daycount)

    def toJson(self):
        return {"idCurrency":self.__idCurrency,"isoCode":self.__isoCode,"currencyNumericCode":self.__currencyNumericCode,"description":self.__description,"minorUnit":self.__minorUnit,"systemCurr":self.__systemCurr,"physicalCurr":self.__physicalCurr,"calendar":self.__calendar.toJson(),"businessDays":self.__businessDays,"daycount":self.__daycount.toJson()}

