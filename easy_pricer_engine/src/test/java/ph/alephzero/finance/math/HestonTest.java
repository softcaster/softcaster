/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.math;

/**
 *
 * @author softc
 */
public class HestonTest {

    public static void main(String[] args) {
        HestonInput input = new HestonInput();
        //100, 100, 1.0, 0.03, 2.0, 0.04, 0.3, -0.7, 0.04
        // S0, K, T, r, kappa, theta, sigma, rho, v0
        input.S0 = 100.;
        input.K = 100.;
        input.T = 1.;
        input.r = 0.03;
        input.kappa = 2.;
        input.theta = 0.04;
        input.sigma = 0.0001;
        input.rho = 0.0001;
        input.v0 = 0.04;

        double price = HestonAnalytic.calculateCallPrice(input);
        System.out.printf("Prezzo della Call (Heston): %.4f%n", price);

    }

}
