import { lazy } from 'react';
import type { TreeNode } from 'primereact/treenode';

// Viste caricate a richiesta; la chiave di un nodo foglia e' il percorso + '-key'
// (App sincronizza la selezione dell'albero con l'URL)
export const HomeView = lazy(() => import('../views/HomeView'));
export const LoanFixedView = lazy(() => import('../views/LoanTradeView').then(m => ({ default: m.LoanFixedView })));
export const LoanFloatingView = lazy(() => import('../views/LoanTradeView').then(m => ({ default: m.LoanFloatingView })));
export const FxAccumulatorView = lazy(() => import('../views/AccumulatorTradeView').then(m => ({ default: m.FxAccumulatorView })));
export const CmdAccumulatorView = lazy(() => import('../views/AccumulatorTradeView').then(m => ({ default: m.CmdAccumulatorView })));
export const LoanPricingView = lazy(() => import('../views/PricingViews').then(m => ({ default: m.LoanPricingView })));
export const FxAccumulatorPricingView = lazy(() => import('../views/PricingViews').then(m => ({ default: m.FxAccumulatorPricingView })));
export const CmdAccumulatorPricingView = lazy(() => import('../views/PricingViews').then(m => ({ default: m.CmdAccumulatorPricingView })));
export const ContractsView = lazy(() => import('../views/ContractsView'));

export const navigationNodes: TreeNode[] = [
    {
        key: 'trades',
        label: 'Trades',
        children: [
            {
                key: 'cash-flow',
                label: 'Cash Flow',
                children: [
                    { key: 'loan-fixed-key', label: 'Loan Fixed', data: '/loan-fixed' },
                    { key: 'loan-floating-key', label: 'Loan Floating', data: '/loan-floating' },
                ]
            },
            {
                key: 'forex',
                label: 'Forex',
                children: [
                    { key: 'fx-accumulator-key', label: 'FX Accumulators', data: '/fx-accumulator' },
                ]
            },
            {
                key: 'commodity',
                label: 'Commodities',
                children: [
                    { key: 'cmd-accumulator-key', label: 'Commodity Accumulators', data: '/cmd-accumulator' },
                ]
            }
        ]
    },
    {
        key: 'pricing',
        label: 'Pricing',
        children: [
            {
                key: 'cash-flow-p',
                label: 'Cash Flow',
                children: [
                    { key: 'loan-p-key', label: 'Loans', data: '/loan-p' },
                ]
            },
            {
                key: 'forex-p',
                label: 'Forex',
                children: [
                    { key: 'fx-accumulator-p-key', label: 'FX Accumulators', data: '/fx-accumulator-p' },
                ]
            },
            {
                key: 'commodity-p',
                label: 'Commodities',
                children: [
                    { key: 'cmd-accumulator-p-key', label: 'Commodity Accumulators', data: '/cmd-accumulator-p' },
                ]
            }
        ]
    },
    {
        key: 'views',
        label: 'Holding Views',
        children: [
            { key: 'contracts-key', label: 'Contracts', data: '/contracts' },
        ]
    }
];
