from dataclasses import dataclass, asdict
from datetime import date
from typing import Optional

@dataclass
class PricingRequest:
    isin: str = ""
    referencePrice: float = 0.0
    referenceDate: Optional[date] = None

@dataclass
class ForwardPricingRequest(PricingRequest):
    domesticRate: float = 0.0
    foreignRate: float = 0.0
