import { lazy } from 'react';
import type { TreeNode } from 'primereact/treenode';

// 1. Definiamo i Lazy Components (le importazioni rimangono qui)
export const ForexView = lazy(() => import('../components/views/ForexView'));
export const FxFutureView = lazy(() => import('../components/views/FxFutureView'));
export const HomeView = lazy(() => import('../components/views/HomeView'));
export const PlaceholderView = lazy(() => import('../components/views/PlaceholderView'));
export const BondView = lazy(() => import('../components/views/BondView.tsx'));
export const XNoteView = lazy(() => import('../components/views/XNoteView.tsx'));

export const navigationNodes: TreeNode[] = [
  {
    key: 'master-data',
    label: 'Trades',
    //icon: 'pi pi-fw pi-building-columns',
    children: [
      {
        key: 'forex-std',
        label: 'Forex',
        //icon: 'pi pi-fw pi-briefcase',
        children: [
          {
            key: 'forex-key',
            label: 'Spot',
            //icon: 'pi pi-fw pi-money-bill',
            data: '/forex' // link
          },
          {
            key: 'fxfuture-key',
            label: 'FX Futures',
            //icon: 'pi pi-fw pi-money-bill',
            data: '/fxfuture' // link
          },
        ]
      },
      {
        key: 'fixed-income',
        label: 'Fixed Income',
        //icon: 'pi pi-fw pi-briefcase',
        children: [
          { key: 'bond-key', label: 'X Bonds', /*icon: 'pi pi-fw pi-building',*/ data: '/bond' },
          { key: 'xnote-key', label: 'X Rate notes', /*icon: 'pi pi-fw pi-building',*/ data: '/xnote' },
          { key: 'bondfuture-key', label: 'Bond Futures', /*icon: 'pi pi-fw pi-building',*/ data: '/bondfuture' }
        ]
      }
    ]
  },
  {
    key: 'pricing',
    label: 'Pricing',
    //icon: 'pi pi-fw pi-calculator',
    children: [
      { key: 'bond-p-key', label: 'Bonds', /*icon: 'pi pi-fw pi-users',*/ data: '/bond-p' },
    ]
  },
  {
    key: 'settings',
    label: 'System Settings',
    //icon: 'pi pi-fw pi-cog',
    children: [
      { key: 'user-key', label: 'User Management', /*icon: 'pi pi-fw pi-users',*/ data: '/user' },
      { key: 'log-key', label: 'Audit Logs', /*icon: 'pi pi-fw pi-list',*/ data: '/log' }
    ]
  } 
];
