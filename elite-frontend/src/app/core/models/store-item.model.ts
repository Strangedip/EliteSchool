export type AcquisitionType = 'POINTS' | 'TASKS' | 'POINTS_AND_TASKS';
export type ItemCategory = 'MATERIAL' | 'OPPORTUNITY';
export type ClaimWindowStatus = 'OPEN' | 'NOT_OPEN' | 'EXPIRED';

export interface RequiredTaskProgress {
  taskId: string;
  title: string;
  completed: boolean;
}

export interface ClaimInfo {
  studentId: string;
  studentName?: string;
  claimedAt?: string;
}

export interface StoreItem {
  id: string;
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl?: string;
  acquisitionType?: AcquisitionType;
  itemCategory?: ItemCategory;
  opportunityBrief?: string;
  intendedAudience?: string;
  eligibilityChecklist?: string[];
  claimOpensAt?: string | null;
  claimClosesAt?: string | null;
  requiredTaskIds?: string[];
  requiredTasks?: RequiredTaskProgress[];
  eligible?: boolean;
  withinClaimWindow?: boolean;
  windowStatus?: ClaimWindowStatus;
  claims?: ClaimInfo[];
}

export interface StorePurchase {
  id: string;
  studentId: string;
  studentName?: string;
  itemId: string;
  itemName: string;
  acquisitionType?: string;
  itemCategory?: string;
  claimedAt: string;
}
