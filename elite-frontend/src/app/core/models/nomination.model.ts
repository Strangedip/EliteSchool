export type NominationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface ContributionNomination {
  id?: string;
  studentId: string;
  nominatedBy?: string;
  nominatorRole?: string;
  suggestedPoints: number;
  awardedPoints?: number;
  reason: string;
  evidenceNote?: string;
  status?: NominationStatus;
  reviewedBy?: string;
  reviewedAt?: string;
  reviewNotes?: string;
  walletReferenceId?: string;
  createdAt?: string;
}
