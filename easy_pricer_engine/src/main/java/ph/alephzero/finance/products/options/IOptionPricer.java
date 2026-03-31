/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ph.alephzero.finance.products.options;

/**
 *
 * @author ep
 */
public interface IOptionPricer {
    public OptionCalcOutputData priceCall(OptionCalcInputData input);
    public OptionCalcOutputData pricePut(OptionCalcInputData input);
}
