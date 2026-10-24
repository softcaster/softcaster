from dataclasses import dataclass
from datetime import date
from typing import Optional

@dataclass
class PricingRequest:
    """Richiesta di pricing unica (PricingRequest in mds_core): ogni endpoint
    legge solo i campi che gli servono; None = non indicato."""
    isin: str = ""
    referencePrice: Optional[float] = None
    referenceDate: Optional[date] = None
    # bond
    yieldCurve: str = ""
    fullCalc: bool = False
    referenceRate: Optional[float] = None
    # forward / future
    domesticRate: Optional[float] = None   # bond future: tasso repo se domesticRCurve e' vuota
    domesticRCurve: str = ""
    foreignRate: Optional[float] = None
    foreignRCurve: str = ""
    maturityDate: Optional[date] = None
