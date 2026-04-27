import { lazy } from 'react';
import type { TreeNode } from 'primereact/treenode';

// 1. Definiamo i Lazy Components (le importazioni rimangono qui)
export const ForexView = lazy(() => import('../components/views/ForexView'));
export const FxFutureView = lazy(() => import('../components/views/FxFutureView'));
export const HomeView  = lazy(() => import('../components/views/HomeView'));
export const PlaceholderView  = lazy(() => import('../components/views/PlaceholderView'));

export const navigationNodes: TreeNode[] = [
  {
    key: 'master-data',
    label: 'Master Data',
    icon: 'pi pi-fw pi-database',
    children: [
      {
        key: 'forex-std',
        label: 'Forex',
        icon: 'pi pi-fw pi-briefcase',
        children: [
          { 
            key: 'forex-key', 
            label: 'Forex (Spot/Fwd)', 
            icon: 'pi pi-fw pi-money-bill',
            data: '/forex' // link
          },
          { 
            key: 'fxfuture-key', 
            label: 'FX Futures', 
            icon: 'pi pi-fw pi-chart-bar', 
            data: '/fxfuture' // link
          },
        ]
      },
      {
        key: 'fixed-income',
        label: 'Fixed Income',
        icon: 'pi pi-fw pi-percentage',
        children: [
          { key: 'bond-key', label: 'Bonds', icon: 'pi pi-fw pi-flag', data: '/bond' },
          { key: 'bondfuture-key', label: 'Bond Futures', icon: 'pi pi-fw pi-building', data: '/bondfuture'  }
        ]
      }
    ]
  },
  {
    key: 'settings',
    label: 'System Settings',
    icon: 'pi pi-fw pi-cog',
    children: [
      { key: 'user-key', label: 'User Management', icon: 'pi pi-fw pi-users', data: '/user' },
      { key: 'log-key', label: 'Audit Logs', icon: 'pi pi-fw pi-list', data: '/log' }
    ]
  }
];
