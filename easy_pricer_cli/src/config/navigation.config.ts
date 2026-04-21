import { lazy } from 'react';
import type { LazyExoticComponent, ComponentType } from 'react';
import type { TreeNode } from 'primereact/treenode';

// 1. Definiamo i Lazy Components (le importazioni rimangono qui)
const ForexView = lazy(() => import('../components/views/ForexView'));
const FxFutureView = lazy(() => import('../components/views/FxFuture'));
// Aggiungi qui gli altri man mano che li crei:
// const BondsView = lazy(() => import('../components/views/BondsView'));

// 2. Mappa dei componenti
export const viewMap: Record<string, LazyExoticComponent<ComponentType<any>>> = {
    'forex-key': ForexView,
    'fxfuture-key': FxFutureView,
};

export const navigationNodes: TreeNode[] = [
  {
    key: 'master-data',
    label: 'Master Data',
    icon: 'pi pi-fw pi-database',
    children: [
      {
        key: 'instruments',
        label: 'Financial Instruments',
        icon: 'pi pi-fw pi-briefcase',
        children: [
          { 
            key: 'forex-key', 
            label: 'Forex (Spot/Fwd)', 
            icon: 'pi pi-fw pi-money-bill',
            data: 'Forex view' // Puoi allegare metadati qui
          },
          { 
            key: 'fxfuture-key', 
            label: 'FX Futures', 
            icon: 'pi pi-fw pi-chart-bar' 
          },
        ]
      },
      {
        key: 'fixed-income',
        label: 'Fixed Income',
        icon: 'pi pi-fw pi-percentage',
        children: [
          { key: 'bonds-gov', label: 'Government Bonds', icon: 'pi pi-fw pi-flag' },
          { key: 'bonds-corp', label: 'Corporate Bonds', icon: 'pi pi-fw pi-building' }
        ]
      }
    ]
  },
  {
    key: 'settings',
    label: 'System Settings',
    icon: 'pi pi-fw pi-cog',
    children: [
      { key: 'users', label: 'User Management', icon: 'pi pi-fw pi-users' },
      { key: 'logs', label: 'Audit Logs', icon: 'pi pi-fw pi-list' }
    ]
  }
];
