package ph.alephzero.finance;

public enum DayCountBasis {
	NASD_30_360(30, 360),
	EUR_30_360(30, 360),
	ACT_360(-1, 360),
	ACT_365(-1, 365),
	ACT_ACT(-1, -1);
	
	private final int daysPerMonth;
	private final int daysPerYear;
	
	private DayCountBasis(int daysPerMonth, int daysPerYear) {
		this.daysPerMonth = daysPerMonth;
		this.daysPerYear = daysPerYear;
	}
	
	public int getDaysPerMonth() {
		return daysPerMonth;
	}
	
	public int getDaysPerYear() {
		return daysPerYear;
	}

        public double getTime() {
            double time = daysPerYear * 1.;
            if(time < 0.) {
                time = 365.;
            }
            
            return time;
	}
}
