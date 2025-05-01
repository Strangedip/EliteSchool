package com.eliteschool.reward_service.dto;

import com.eliteschool.reward_service.model.RewardWallet;
import java.util.List;
import java.util.stream.Collectors;

public class RewardWalletMapper {

    public static RewardWalletDto toDto(RewardWallet wallet) {
        if (wallet == null) {
            return null;
        }
        
        return RewardWalletDto.builder()
                .studentId(wallet.getStudentId())
                .balance(wallet.getBalance())
                .build();
    }
    
    public static RewardWallet toEntity(RewardWalletDto dto) {
        if (dto == null) {
            return null;
        }
        
        return RewardWallet.builder()
                .studentId(dto.getStudentId())
                .balance(dto.getBalance())
                .build();
    }
    
    public static List<RewardWalletDto> toDtoList(List<RewardWallet> wallets) {
        if (wallets == null) {
            return List.of();
        }
        
        return wallets.stream()
                .map(RewardWalletMapper::toDto)
                .collect(Collectors.toList());
    }
} 