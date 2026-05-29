if(txn.getMasterData().getAssetClass().getCode().equals("FSP"))
    return "FSP";
else
    return "#OTHER: " + txn.getMasterData().getAssetClass().getCode();
